package io.emeraldpay.pjc.apiws

import com.fasterxml.jackson.databind.ObjectMapper
import io.emeraldpay.pjc.api.RpcCall
import io.emeraldpay.pjc.api.RpcException
import io.emeraldpay.pjc.api.SubscribeCall
import io.emeraldpay.pjc.json.BlockJson
import io.emeraldpay.pjc.types.Hash256
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
    PolkadotWsApi client

    def setup() {
        port++
        server = new MockWsServer(port)
        server.start()
        Thread.sleep(SLEEP)
        client = PolkadotWsApi.newBuilder()
                .connectTo("ws://localhost:${port}")
                .build()
        assert client.connect().get(TIMEOUT, TimeUnit.SECONDS)
    }

    def cleanup() {
        client.close()
        server.stop()
    }

    def "Subscribe to block"() {
        setup:
        List<Map<String, Object>> received = []
        when:
        server.onNextReply('{"jsonrpc":"2.0","result":101,"id":0}')
        def f = client.subscribe(SubscribeCall.create(BlockJson.Header.class, "chain_subscribeNewHead", "chain_unsubscribeNewHead"))
        def sub = f.get(TIMEOUT, TimeUnit.SECONDS)
        sub.handler({ event ->
            received.add([
                    method: event.method,
                    result: event.result
            ])
        })
        server.reply('{"jsonrpc":"2.0","method":"chain_newHead","params":{"result":{"digest":{"logs":[]},"extrinsicsRoot":"0x9869230c3cc05051ce9afef4458d2515fb2141bfd3bdcd88292f41e17ea00ae7","number":"0x1d878c","parentHash":"0xbe9110f6da6a19ac645a27472e459dcca6eaf4ee4b0b12700ca5d566eea9a638","stateRoot":"0x57059722d680b591a469937449df772b95625d4230b39a0a7d855e16d597f168"},"subscription":101}}')
        Thread.sleep(SLEEP)
        sub.close()
        Thread.sleep(SLEEP)
        then:
        received.size() == 1
        received[0]["method"] == "chain_newHead"
        received[0]["result"] instanceof BlockJson.Header

        server.received.size() == 2
        server.received[0].value == '{"jsonrpc":"2.0","id":0,"method":"chain_subscribeNewHead","params":[]}'
        server.received[1].value == '{"jsonrpc":"2.0","id":1,"method":"chain_unsubscribeNewHead","params":[101]}'
    }

    def "Make a request"() {
        when:
        server.onNextReply('{"jsonrpc":"2.0","result":"Hello World!","id":0}')
        def f = client.execute(RpcCall.create(String.class, "test_foo"))
        def act = f.get(TIMEOUT, TimeUnit.SECONDS)
        then:
        act == "Hello World!"
    }

    def "Works with provided HttpClient"() {
        setup:
        client.close()
        client = PolkadotWsApi.newBuilder()
            .httpClient(HttpClient.newHttpClient())
            .connectTo("ws://localhost:${port}")
            .build()
        client.connect().get(TIMEOUT, TimeUnit.SECONDS)
        when:
        server.onNextReply('{"jsonrpc":"2.0","result":"Hello World!","id":0}')
        def f = client.execute(RpcCall.create(String.class, "test_foo"))
        def act = f.get(TIMEOUT, TimeUnit.SECONDS)
        then:
        act == "Hello World!"
    }

    def "Works with provided ObjectMapper"() {
        setup:
        ObjectMapper objectMapper = Spy(new ObjectMapper())
        client.close()
        client = PolkadotWsApi.newBuilder()
                .objectMapper(objectMapper)
                .connectTo("ws://localhost:${port}")
                .build()
        client.connect().get(TIMEOUT, TimeUnit.SECONDS)
        when:
        server.onNextReply('{"jsonrpc":"2.0","result":"Hello World!","id":0}')
        def f = client.execute(RpcCall.create(String.class, "test_foo"))
        def act = f.get(TIMEOUT, TimeUnit.SECONDS)
        then:
        act == "Hello World!"
        (1.._) * objectMapper._(_, _)
    }

    def "By default connects to 9944"() {
        setup:
        client.close()
        server.stop()
        println("Start new on 9944")
        server = new MockWsServer(9944)
        server.start()
        Thread.sleep(SLEEP)
        client = PolkadotWsApi.newBuilder()
                .build()
        client.connect().get(TIMEOUT, TimeUnit.SECONDS)
        when:
        server.onNextReply('{"jsonrpc":"2.0","result":"Hello World!","id":0}')
        def f = client.execute(RpcCall.create(String.class, "test_foo"))
        def act = f.get(TIMEOUT, TimeUnit.SECONDS)
        then:
        act == "Hello World!"
    }

    def "Fail to subscribe with invalid command"() {
        when:
        server.onNextReply('{"jsonrpc":"2.0","error":{"code": -1, "message": "Test"},"id":0}')
        def f = client.subscribe(SubscribeCall.create(Hash256.class, "test_subscribeNone", "test_unsubscribeNone"))
        f.get(TIMEOUT, TimeUnit.SECONDS)
        then:
        def t = thrown(ExecutionException.class)
        t.cause instanceof RpcException
        with((RpcException)t.cause) {
            code == -1
            rpcMessage == "Test"
        }
    }

    def "Ignores unknown responses"() {
        when:
        def f = client.execute(RpcCall.create(String.class, "test_foo"))
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
