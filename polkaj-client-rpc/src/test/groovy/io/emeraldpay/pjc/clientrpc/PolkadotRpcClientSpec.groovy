package io.emeraldpay.pjc.clientrpc

import com.fasterxml.jackson.databind.JavaType
import io.emeraldpay.pjc.json.BlockJson
import io.emeraldpay.pjc.json.BlockResponseJson
import io.emeraldpay.pjc.types.Hash256
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.MediaType
import spock.lang.Specification

import java.util.concurrent.CompletionException
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService

class PolkadotRpcClientSpec extends Specification {

    PolkadotRpcClient client

    def setup() {
        client = PolkadotRpcClient.newBuilder()
            .connectTo("http://localhost:18080")
            .build()
    }

    def "Encode empty params request"() {
        when:
        def act = client.encode(1, "test_foo")
        then:
        new String(act) == '{"jsonrpc":"2.0","id":1,"method":"test_foo","params":[]}'
    }

    def "Encode single param request"() {
        when:
        def act = client.encode(1, "test_foo", "hello")
        then:
        new String(act) == '{"jsonrpc":"2.0","id":1,"method":"test_foo","params":["hello"]}'
    }

    def "Encode multi params request"() {
        when:
        def act = client.encode(2, "test_foo", Hash256.from("0x5d83f66b61701da4cbd7a60137db89c69469a4f798b62aba9176ab253b423828"), 100, false)
        then:
        new String(act) == '{"jsonrpc":"2.0","id":2,"method":"test_foo","params":["0x5d83f66b61701da4cbd7a60137db89c69469a4f798b62aba9176ab253b423828",100,false]}'
    }

    def "Encode object params request"() {
        when:
        def act = client.encode(3, "test_foo", [foo: "bar", baz: 1])
        then:
        new String(act) == '{"jsonrpc":"2.0","id":3,"method":"test_foo","params":[{"foo":"bar","baz":1}]}'
    }

    def "Decode basic"() {
        setup:
        def response = '{\n' +
                '  "jsonrpc": "2.0",\n' +
                '  "result": "0x5d83f66b61701da4cbd7a60137db89c69469a4f798b62aba9176ab253b423828",\n' +
                '  "id": 0\n' +
                '}'
        when:
        def act = client.decode(0, response, client.responseType(String))
        then:
        act == '0x5d83f66b61701da4cbd7a60137db89c69469a4f798b62aba9176ab253b423828'
    }

    def "Decode null"() {
        setup:
        def response = '{\n' +
                '  "jsonrpc": "2.0",\n' +
                '  "result": null,\n' +
                '  "id": 0\n' +
                '}'
        when:
        def act = client.decode(0, response, client.responseType(BlockResponseJson))
        then:
        act == null
    }

    def "Decode Hash256"() {
        setup:
        def response = '{\n' +
                '  "jsonrpc": "2.0",\n' +
                '  "result": "0x5d83f66b61701da4cbd7a60137db89c69469a4f798b62aba9176ab253b423828",\n' +
                '  "id": 0\n' +
                '}'
        when:
        def act = client.decode(0, response, client.responseType(Hash256))
        then:
        act == Hash256.from('0x5d83f66b61701da4cbd7a60137db89c69469a4f798b62aba9176ab253b423828')
    }

    def "Decode error"() {
        setup:
        def response = '{\n' +
                '  "jsonrpc": "2.0",\n' +
                '  "error": {"code": 100, "message": "Test error", "details": 1},\n' +
                '  "id": 0\n' +
                '}'
        when:
        client.decode(0, response, client.responseType(BlockResponseJson))
        then:
        def t = thrown(CompletionException)
        t.cause instanceof RpcException
        with((RpcException)t.cause) {
            code == 100
            rpcMessage == "Test error"
        }
    }

    def "Fail to decode if wrong id"() {
        setup:
        def response = '{\n' +
                '  "jsonrpc": "2.0",\n' +
                '  "result": "0x5d83f66b61701da4cbd7a60137db89c69469a4f798b62aba9176ab253b423828",\n' +
                '  "id": 0\n' +
                '}'
        when:
        client.decode(1, response, client.responseType(String))
        then:
        def t = thrown(CompletionException)
        t.cause instanceof RpcException
    }

    def "Fail to decode if invalid json"() {
        setup:
        def response = '{\n  "jsonrpc": "2'
        when:
        client.decode(1, response, client.responseType(String))
        then:
        def t = thrown(CompletionException)
        t.cause instanceof RpcException
    }

    def "Make request"() {
        setup:
        ClientAndServer mockServer = ClientAndServer.startClientAndServer(18080)
        def response = '{\n' +
                '  "jsonrpc": "2.0",\n' +
                '  "result": "0x5d83f66b61701da4cbd7a60137db89c69469a4f798b62aba9176ab253b423828",\n' +
                '  "id": 0\n' +
                '}'
        mockServer.when(
                HttpRequest.request()
        ).respond(
                HttpResponse.response(response).withContentType(MediaType.APPLICATION_JSON)
        )
        when:
        def act = client.execute(String, "chain_getFinalisedHead")
        then:
        act.get() == "0x5d83f66b61701da4cbd7a60137db89c69469a4f798b62aba9176ab253b423828"

        cleanup:
        mockServer.stop()
    }

    def "Doesn't make requests after close"() {
        setup:
        ClientAndServer mockServer = ClientAndServer.startClientAndServer(18080)
        def response = '{\n' +
                '  "jsonrpc": "2.0",\n' +
                '  "result": "0x5d83f66b61701da4cbd7a60137db89c69469a4f798b62aba9176ab253b423828",\n' +
                '  "id": 0\n' +
                '}'
        mockServer.when(
                HttpRequest.request()
        ).respond(
                HttpResponse.response(response).withContentType(MediaType.APPLICATION_JSON)
        )
        when:
        def act = client.execute(String, "chain_getFinalisedHead")
        then:
        act.get() != null

        when:
        client.close()
        client.execute(String, "chain_getFinalisedHead").get()

        then:
        def t = thrown(ExecutionException)
        t.cause instanceof IllegalStateException

        cleanup:
        mockServer.stop()
    }

    def "Make request for complex object"() {
        setup:
        ClientAndServer mockServer = ClientAndServer.startClientAndServer(18080)
        def response = '{' +
                '  "jsonrpc": "2.0",' +
                '  "result": {' +
                '    "block": {' +
                '      "extrinsics": [],' +
                '      "header": {' +
                '        "digest": {' +
                '          "logs": []' +
                '        },' +
                '        "extrinsicsRoot": "0xeaa154a7541c9ed34218b89c1f5e4add2976329ba830543a72d8115c61725212",' +
                '        "number": "0x401a1",' +
                '        "parentHash": "0xb5ba465c2e20fd844d9f0a8ee34680ced6121544af22cffa8236af9ca00d13b1",' +
                '        "stateRoot": "0x0984f5c13d7d271467332697fa5fc191539c1441f5ca1b234618ff25638b7d66"' +
                '      }' +
                '    },' +
                '    "justification": null' +
                '  },' +
                '  "id": 0' +
                '}'
        mockServer.when(
                HttpRequest.request()
        ).respond(
                HttpResponse.response(response).withContentType(MediaType.APPLICATION_JSON)
        )
        when:
        def act = client.execute(BlockResponseJson, "chain_getBlock", Hash256.from("0x9130103f8fbca52a79042211383946b39e6269b6ab49bc08035c9893d782c1bb")).get()
        then:
        mockServer.verify(
                HttpRequest.request()
                        .withBody('{"jsonrpc":"2.0","id":0,"method":"chain_getBlock","params":["0x9130103f8fbca52a79042211383946b39e6269b6ab49bc08035c9893d782c1bb"]}')
        )

        act.justification == null
        act.block != null
        with(act.block) {
            header.number == 0x401a1
            header.extrinsicsRoot == Hash256.from("0xeaa154a7541c9ed34218b89c1f5e4add2976329ba830543a72d8115c61725212")
        }

        cleanup:
        mockServer.stop()
    }

    def "Fail if non-200 status"() {
        setup:
        ClientAndServer mockServer = ClientAndServer.startClientAndServer(18080)
        mockServer.when(
                HttpRequest.request()
        ).respond(
                HttpResponse.response("").withContentType(MediaType.APPLICATION_JSON).withStatusCode(503)
        )
        when:
        client.execute(String, "chain_getFinalisedHead").get()
        then:
        def t = thrown(ExecutionException)
        t.cause instanceof RpcException
        with((RpcException)t.cause) {
            code == -32000
            rpcMessage.contains("503")
        }

        cleanup:
        mockServer.stop()
    }

    def "Fail if non-JSON content"() {
        setup:
        ClientAndServer mockServer = ClientAndServer.startClientAndServer(18080)
        mockServer.when(
                HttpRequest.request()
        ).respond(
                HttpResponse.response("").withContentType(MediaType.TEXT_PLAIN)
        )
        when:
        client.execute(String, "chain_getFinalisedHead").get()
        then:
        def t = thrown(ExecutionException)
        t.cause instanceof RpcException
        t.cause.code == -32000

        cleanup:
        mockServer.stop()
    }

    def "Send basic auth"() {
        setup:
        client = PolkadotRpcClient.newBuilder()
                .connectTo("http://localhost:18080")
                .basicAuth("testuser", "testpassword")
                .build()
        def response = '{' +
                '  "jsonrpc": "2.0",' +
                '  "result": "0x5d83f66b61701da4cbd7a60137db89c69469a4f798b62aba9176ab253b423828",' +
                '  "id": 0' +
                '}'
        ClientAndServer mockServer = ClientAndServer.startClientAndServer(18080)
        mockServer.when(
                HttpRequest.request()
        ).respond(
                HttpResponse.response(response).withContentType(MediaType.APPLICATION_JSON)
        )
        when:
        def act = client.execute(String, "chain_getFinalisedHead").get()
        then:
        act == "0x5d83f66b61701da4cbd7a60137db89c69469a4f798b62aba9176ab253b423828"
        mockServer.verify(
                HttpRequest.request().withHeader("Authorization", "Basic dGVzdHVzZXI6dGVzdHBhc3N3b3Jk")
        )

        cleanup:
        mockServer.stop()
    }

    def "Process response error"() {
        setup:
        client = PolkadotRpcClient.newBuilder()
                .connectTo("http://localhost:18080")
                .build()
        def response = '{' +
                '  "jsonrpc": "2.0",' +
                '  "error": {"code": -32601, "message": "Method not found"},' +
                '  "id": 0' +
                '}'
        ClientAndServer mockServer = ClientAndServer.startClientAndServer(18080)
        mockServer.when(
                HttpRequest.request()
        ).respond(
                HttpResponse.response(response).withContentType(MediaType.APPLICATION_JSON)
        )
        when:
        client.execute(String, "chain_getFinalisedHead").get()
        then:
        def t = thrown(ExecutionException)
        t.cause instanceof RpcException
        with((RpcException)t.cause) {
            code == -32601
            rpcMessage == "Method not found"
        }

        cleanup:
        mockServer.stop()
    }

}
