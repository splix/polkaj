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
        def act = objectMapper.readValue(json, MethodsJson)
        def act2 = objectMapper.readValue(json, MethodsJson)
        then:
        act == act2
        act.hashCode() == act2.hashCode()
    }

    def "Diff data is not equal"() {
        setup:
        String json = BlockJsonSpec.classLoader.getResourceAsStream("other/methods.json").text
        when:
        def act = objectMapper.readValue(json, MethodsJson)
        def act2 = objectMapper.readValue(json, MethodsJson)
        act.methods.remove(0)
        then:
        act != act2
    }

}
