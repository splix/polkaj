package io.emeraldpay.pjc.json

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

class MethodsJsonSpec extends Specification {

    ObjectMapper objectMapper = JsonSpecCommons.objectMapper

    def "Deserialize"() {
        setup:
        InputStream json = BlockJsonSpec.classLoader.getResourceAsStream("other/methods.json")
        when:
        def act = objectMapper.readValue(json, MethodsJson)
        then:
        act != null
        act.version == 1
        act.methods.size() > 0
        act.methods.containsAll(["author_submitExtrinsic", "chain_subscribeAllHeads", "payment_queryInfo", "system_addReservedPeer"])
    }

    def "Same data is equal"() {
        setup:
        String json = BlockJsonSpec.classLoader.getResourceAsStream("other/methods.json").text
        when:
        def x = objectMapper.readValue(json, MethodsJson)
        def y = objectMapper.readValue(json, MethodsJson)
        then:
        x == y
        x == x
        x.hashCode() == y.hashCode()
    }

    def "Diff types are not equal"() {
        setup:
        String json = BlockJsonSpec.classLoader.getResourceAsStream("other/methods.json").text
        when:
        def x = objectMapper.readValue(json, MethodsJson)
        then:
        x != json
    }

    def "Diff data is not equal"() {
        setup:
        String json = BlockJsonSpec.classLoader.getResourceAsStream("other/methods.json").text
        when:
        def x = objectMapper.readValue(json, MethodsJson)
        def y = objectMapper.readValue(json, MethodsJson)
        x.methods.remove(0)
        then:
        x != y
    }

}
