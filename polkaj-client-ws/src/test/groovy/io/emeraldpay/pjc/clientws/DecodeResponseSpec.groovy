package io.emeraldpay.pjc.clientws

import com.fasterxml.jackson.databind.ObjectMapper
import io.emeraldpay.pjc.json.BlockJson
import io.emeraldpay.pjc.json.jackson.PolkadotModule
import io.emeraldpay.pjc.types.Hash256
import spock.lang.Specification

import java.util.concurrent.CompletableFuture

class DecodeResponseSpec extends Specification {

    ObjectMapper objectMapper = new ObjectMapper().tap {
        registerModule(new PolkadotModule())
    }

    def "Decode rpc response with number"() {
        setup:
        def json = '{"jsonrpc":"2.0","result":5,"id":1}'
        def mapping = Mock(DecodeResponse.TypeMapping) {
            1 * get(1) >> objectMapper.typeFactory.constructType(Integer.class)
        }
        def decoder = new DecodeResponse(objectMapper, mapping, Stub(DecodeResponse.TypeMapping))
        when:
        def act = decoder.decode(json)
        then:
        act.type == WsResponse.Type.RPC
        with(act.asRpc()) {
            result instanceof Integer
            result == 5
        }
    }

    def "Decode rpc response with number, when id comes first"() {
        setup:
        def json = '{"jsonrpc":"2.0","id":3,"result":5}'
        def mapping = Mock(DecodeResponse.TypeMapping) {
            1 * get(3) >> objectMapper.typeFactory.constructType(Integer.class)
        }
        def decoder = new DecodeResponse(objectMapper, mapping, Stub(DecodeResponse.TypeMapping))
        when:
        def act = decoder.decode(json)
        then:
        act.type == WsResponse.Type.RPC
        with(act.asRpc()) {
            result instanceof Integer
            result == 5
        }
    }

    def "Decode rpc response with hash"() {
        setup:
        def json = '{"jsonrpc":"2.0","result":"0x9e3c23f49460755ba20511f483cf50759edef63613a566ef19b011ee22895d0a","id":1}'
        def mapping = Mock(DecodeResponse.TypeMapping) {
            1 * get(1) >> objectMapper.typeFactory.constructType(Hash256.class)
        }
        def decoder = new DecodeResponse(objectMapper, mapping, Stub(DecodeResponse.TypeMapping))
        when:
        def act = decoder.decode(json)
        then:
        act.type == WsResponse.Type.RPC
        with(act.asRpc()) {
            result instanceof Hash256
            result == Hash256.from("0x9e3c23f49460755ba20511f483cf50759edef63613a566ef19b011ee22895d0a")
        }
    }

    def "Decode rpc response with block"() {
        setup:
        def json = '{"jsonrpc":"2.0","result":{"header":{"extrinsicsRoot":"0x9e3c23f49460755ba20511f483cf50759edef63613a566ef19b011ee22895d0a","number":"0x1fde78"}},"id":1}'
        def mapping = Mock(DecodeResponse.TypeMapping) {
            1 * get(1) >> objectMapper.typeFactory.constructType(BlockJson.class)
        }
        def decoder = new DecodeResponse(objectMapper, mapping, Stub(DecodeResponse.TypeMapping))
        when:
        def act = decoder.decode(json)
        then:
        act.type == WsResponse.Type.RPC
        with(act.asRpc()) {
            result instanceof BlockJson
            with((BlockJson)result) {
                header.extrinsicsRoot == Hash256.from("0x9e3c23f49460755ba20511f483cf50759edef63613a566ef19b011ee22895d0a")
                header.number == 0x1fde78
            }
        }
    }

    def "Decode rpc response with error"() {
        setup:
        def json = '{"jsonrpc":"2.0","error":{"code":-32602,"message":"Invalid parameters: No parameters were expected","data":"Array([Number(12)])"},"id":3}'
        def mapping = Mock(DecodeResponse.TypeMapping) {
            1 * get(3) >> objectMapper.typeFactory.constructType(BlockJson.class)
        }
        def decoder = new DecodeResponse(objectMapper, mapping, Stub(DecodeResponse.TypeMapping))
        when:
        def act = decoder.decode(json)
        then:
        act.type == WsResponse.Type.RPC
        with(act.asRpc()) {
            error != null
            result == null
            with(error) {
                code == -32602
                message == "Invalid parameters: No parameters were expected"
            }
        }
    }

    def "Decode rpc response with error when id comes first"() {
        setup:
        def json = '{"jsonrpc":"2.0","id":3,"error":{"code":-32602,"message":"Invalid parameters: No parameters were expected"}}'
        def mapping = Mock(DecodeResponse.TypeMapping) {
            1 * get(3) >> objectMapper.typeFactory.constructType(BlockJson.class)
        }
        def decoder = new DecodeResponse(objectMapper, mapping, Stub(DecodeResponse.TypeMapping))
        when:
        def act = decoder.decode(json)
        then:
        act.type == WsResponse.Type.RPC
        with(act.asRpc()) {
            error != null
            result == null
            with(error) {
                code == -32602
                message == "Invalid parameters: No parameters were expected"
            }
        }
    }

    def "Decode subscription response to chain_newHead"() {
        setup:
        def json = '{' +
                '"jsonrpc":"2.0",' +
                '"method":"chain_newHead",' +
                '"params":{' +
                '  "result":{' +
                '     "digest":{"logs":["0x06424142453402d90000004077c40f00000000","0x05424142450101a0085dbd50d943878845263fa4d2bd8259cde78692f1e22488227843057d5a3101909f2bdfb492e6da3f63413366c9e189c7bf4bd62ae10607fe0c1550dc4d88"]},' +
                '     "extrinsicsRoot":"0x9869230c3cc05051ce9afef4458d2515fb2141bfd3bdcd88292f41e17ea00ae7",' +
                '     "number":"0x1d878c",' +
                '     "parentHash":"0xbe9110f6da6a19ac645a27472e459dcca6eaf4ee4b0b12700ca5d566eea9a638",' +
                '     "stateRoot":"0x57059722d680b591a469937449df772b95625d4230b39a0a7d855e16d597f168"' +
                '  },' +
                '  "subscription":3' +
                ' }' +
                '}'
        def mapping = Mock(DecodeResponse.TypeMapping) {
            1 * get(3) >> objectMapper.typeFactory.constructType(BlockJson.Header.class)
        }
        def decoder = new DecodeResponse(objectMapper, Stub(DecodeResponse.TypeMapping), mapping)
        when:
        def act = decoder.decode(json)
        then:
        act.type == WsResponse.Type.SUBSCRIPTION
        PolkadotWsClient.SubscriptionResponse event = act.asEvent()
        event.method == "chain_newHead"
        event.id == 3
        event.value instanceof BlockJson.Header
        with((BlockJson.Header)event.value) {
            number == 0x1d878c
            parentHash == Hash256.from("0xbe9110f6da6a19ac645a27472e459dcca6eaf4ee4b0b12700ca5d566eea9a638")
            digest != null
            digest.logs.size() == 2
        }
    }

    def "Decode subscription response to chain_newHead when id comes before result"() {
        setup:
        def json = '{' +
                '"jsonrpc":"2.0",' +
                '"method":"chain_newHead",' +
                '"params":{' +
                '  "subscription":3,' +
                '  "result":{' +
                '     "digest":{"logs":["0x06424142453402d90000004077c40f00000000","0x05424142450101a0085dbd50d943878845263fa4d2bd8259cde78692f1e22488227843057d5a3101909f2bdfb492e6da3f63413366c9e189c7bf4bd62ae10607fe0c1550dc4d88"]},' +
                '     "extrinsicsRoot":"0x9869230c3cc05051ce9afef4458d2515fb2141bfd3bdcd88292f41e17ea00ae7",' +
                '     "number":"0x1d878c",' +
                '     "parentHash":"0xbe9110f6da6a19ac645a27472e459dcca6eaf4ee4b0b12700ca5d566eea9a638",' +
                '     "stateRoot":"0x57059722d680b591a469937449df772b95625d4230b39a0a7d855e16d597f168"' +
                '  }' +
                ' }' +
                '}'
        def mapping = Mock(DecodeResponse.TypeMapping) {
            1 * get(3) >> objectMapper.typeFactory.constructType(BlockJson.Header.class)
        }
        def decoder = new DecodeResponse(objectMapper, Stub(DecodeResponse.TypeMapping), mapping)
        when:
        def act = decoder.decode(json)
        then:
        act.type == WsResponse.Type.SUBSCRIPTION
        PolkadotWsClient.SubscriptionResponse event = act.asEvent()
        event.method == "chain_newHead"
        event.id == 3
        event.value instanceof BlockJson.Header
        with((BlockJson.Header)event.value) {
            number == 0x1d878c
            parentHash == Hash256.from("0xbe9110f6da6a19ac645a27472e459dcca6eaf4ee4b0b12700ca5d566eea9a638")
            digest != null
            digest.logs.size() == 2
        }
    }
}
