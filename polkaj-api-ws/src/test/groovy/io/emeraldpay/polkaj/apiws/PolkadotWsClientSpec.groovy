package io.emeraldpay.polkaj.apiws

import com.fasterxml.jackson.databind.ObjectMapper
import io.emeraldpay.polkaj.api.PolkadotApi
import io.emeraldpay.polkaj.api.RpcCall
import io.emeraldpay.polkaj.api.RpcCoder
import io.emeraldpay.polkaj.api.RpcException
import io.emeraldpay.polkaj.api.SubscribeCall
import io.emeraldpay.polkaj.json.BlockJson
import io.emeraldpay.polkaj.types.Hash256
import spock.lang.Shared
import spock.lang.Specification

import java.net.http.HttpClient
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

class PolkadotWsClientSpec extends Specification {

    // needs large timeouts and sleep, especially on CI where it's much slower to run
    static TIMEOUT = 15
    static SLEEP = 250

//        System.setProperty("jdk.httpclient.HttpClient.log", "all");
//        System.setProperty("jdk.internal.httpclient.websocket.debug", "true")

    // port may be held open a couple of seconds after stopping the test, need new port each time to avoid collision
    static int port = 19900 + new Random().nextInt(100)
    @Shared
    MockWsServer server
    @Shared
    JavaHttpSubscriptionAdapter adapter
    @Shared
    PolkadotApi polkadotApi

    def setup() {
        port++
        server = new MockWsServer(port)
        server.start()
        Thread.sleep(SLEEP)
        adapter = JavaHttpSubscriptionAdapter.newBuilder()
                .connectTo("ws://localhost:${port}")
                .build()

        polkadotApi = PolkadotApi.newBuilder()
                .rpcCallAdapter(adapter)
                .subscriptionAdapter(adapter).build()

        assert adapter.connect().get(TIMEOUT, TimeUnit.SECONDS)
    }

    def cleanup() {
        adapter.close()
        server.stop()
    }

    def "Subscribe to block"() {
        setup:
        List<Map<String, Object>> received = []
        when:
        server.onNextReply('{"jsonrpc":"2.0","result":"EsqruyKPnZvPZ6fr","id":0}')
        def f = adapter.subscribe(SubscribeCall.create(BlockJson.Header.class, "chain_subscribeNewHead", "chain_unsubscribeNewHead"))
        def sub = f.get(TIMEOUT, TimeUnit.SECONDS)
        sub.handler({ event ->
            received.add([
                    method: event.method,
                    result: event.result
            ])
        })
        server.reply('{"jsonrpc":"2.0","method":"chain_newHead","params":{"result":{"digest":{"logs":[]},"extrinsicsRoot":"0x9869230c3cc05051ce9afef4458d2515fb2141bfd3bdcd88292f41e17ea00ae7","number":"0x1d878c","parentHash":"0xbe9110f6da6a19ac645a27472e459dcca6eaf4ee4b0b12700ca5d566eea9a638","stateRoot":"0x57059722d680b591a469937449df772b95625d4230b39a0a7d855e16d597f168"},"subscription":"EsqruyKPnZvPZ6fr"}}')
        Thread.sleep(SLEEP)
        sub.close()
        Thread.sleep(SLEEP)
        then:
        received.size() == 1
        received[0]["method"] == "chain_newHead"
        received[0]["result"] instanceof BlockJson.Header

        server.received.size() == 2
        server.received[0].value == '{"jsonrpc":"2.0","id":0,"method":"chain_subscribeNewHead","params":[]}'
        server.received[1].value == '{"jsonrpc":"2.0","id":1,"method":"chain_unsubscribeNewHead","params":["EsqruyKPnZvPZ6fr"]}'
    }

    def "Make a request"() {
        when:
        server.onNextReply('{"jsonrpc":"2.0","result":"Hello World!","id":0}')
        def f = polkadotApi.execute(RpcCall.create(String.class, "test_foo"))
        def act = f.get(TIMEOUT, TimeUnit.SECONDS)
        then:
        act == "Hello World!"
    }

    def "Works with provided HttpClient"() {
        setup:
        adapter.close()
        adapter = JavaHttpSubscriptionAdapter.newBuilder()
            .httpClient(HttpClient.newHttpClient())
            .connectTo("ws://localhost:${port}")
            .build()

        polkadotApi = PolkadotApi.newBuilder().rpcCallAdapter(adapter).rpcCallAdapter(adapter).build()
        adapter.connect().get(TIMEOUT, TimeUnit.SECONDS)
        when:
        server.onNextReply('{"jsonrpc":"2.0","result":"Hello World!","id":0}')
        def f = polkadotApi.execute(RpcCall.create(String.class, "test_foo"))
        def act = f.get(TIMEOUT, TimeUnit.SECONDS)
        then:
        act == "Hello World!"
    }

    def "Works with provided RpcCoder"() {
        setup:
        RpcCoder rpcCoder = Spy(new RpcCoder(new ObjectMapper()))
        adapter.close()
        adapter = JavaHttpSubscriptionAdapter.newBuilder()
                .rpcCoder(rpcCoder)
                .connectTo("ws://localhost:${port}")
                .build()
        adapter.connect().get(TIMEOUT, TimeUnit.SECONDS)
        when:
        server.onNextReply('{"jsonrpc":"2.0","result":"Hello World!","id":0}')
        def f = adapter.produceRpcFuture(RpcCall.create(String.class, "test_foo"))
        def act = f.get(TIMEOUT, TimeUnit.SECONDS)
        then:
        act == "Hello World!"
        1 * rpcCoder.nextId()
    }

    def "By default connects to 9944"() {
        setup:
        adapter.close()
        server.stop()
        println("Start new on 9944")
        server = new MockWsServer(9944)
        server.start()
        Thread.sleep(SLEEP)
        adapter = JavaHttpSubscriptionAdapter.newBuilder()
                .build()
        adapter.connect().get(TIMEOUT, TimeUnit.SECONDS)
        polkadotApi = PolkadotApi.newBuilder().subscriptionAdapter(adapter).rpcCallAdapter(adapter).build()
        when:
        server.onNextReply('{"jsonrpc":"2.0","result":"Hello World!","id":0}')
        def f = polkadotApi.execute(RpcCall.create(String.class, "test_foo"))
        def act = f.get(TIMEOUT, TimeUnit.SECONDS)
        then:
        act == "Hello World!"
    }

    def "Fail to subscribe with invalid command"() {
        when:
        server.onNextReply('{"jsonrpc":"2.0","error":{"code": -1, "message": "Test", "data": "Test data"},"id":0}')
        def f = polkadotApi.subscribe(SubscribeCall.create(Hash256.class, "test_subscribeNone", "test_unsubscribeNone"))
        f.get(TIMEOUT, TimeUnit.SECONDS)
        then:
        def t = thrown(ExecutionException.class)
        t.cause instanceof RpcException
        with((RpcException)t.cause) {
            code == -1
            rpcMessage == "Test"
            rpcData == "Test data"
        }
    }

    def "Ignores unknown responses"() {
        when:
        def f = polkadotApi.execute(RpcCall.create(String.class, "test_foo"))
        Thread.sleep(SLEEP)
        server.reply('{"jsonrpc":"2.0","method":"test_none","params":{"result": "test", "subscription":101}}')
        server.reply('{"jsonrpc":"2.0","error":{"code": -1, "message": "Test"},"id":50}')
        server.reply('{"jsonrpc":"2.0","result":"wrong","id":5}')
        server.reply('{"jsonrpc":"2.0","result":"right","id":0}')
        def act = f.get(TIMEOUT, TimeUnit.SECONDS)
        then:
        act == "right"
    }

}
