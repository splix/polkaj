package io.emeraldpay.pjc.client

import com.fasterxml.jackson.databind.ObjectMapper
import io.emeraldpay.pjc.json.BlockResponseJson
import io.emeraldpay.pjc.json.jackson.PolkadotModule
import io.emeraldpay.pjc.types.Hash256
import spock.lang.Specification

import java.util.concurrent.CompletionException

class AbstractPolkadotClientSpec extends Specification {

    AbstractPolkadotClient client = new TestingPolkadotClient(new ObjectMapper().tap { registerModule(new PolkadotModule())})

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

    class TestingPolkadotClient extends AbstractPolkadotClient {
        TestingPolkadotClient(ObjectMapper objectMapper) {
            super(objectMapper)
        }
    }
}
