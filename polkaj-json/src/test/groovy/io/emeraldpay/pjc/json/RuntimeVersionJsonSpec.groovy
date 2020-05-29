package io.emeraldpay.pjc.json

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

class RuntimeVersionJsonSpec extends Specification {

    ObjectMapper objectMapper = JsonSpecCommons.objectMapper

    def "Deserialize"() {
        setup:
        InputStream json = BlockJsonSpec.classLoader.getResourceAsStream("chain/runtimeVersion.json")
        when:
        def act = objectMapper.readValue(json, RuntimeVersionJson)
        then:
        act != null
        act.apis.size() == 12
        act.apis[0] == ["0xdf6acb689907609b", 3]
        act.apis[4] == ["0xf78b278be53f454c", 2]
        act.authoringVersion == 2
        act.implName == "parity-kusama"
        act.implVersion == 0
        act.specName == "kusama"
        act.specVersion == 1062
        act.transactionVersion == 1
    }

    def "Same are equal"() {
        setup:
        String json = BlockJsonSpec.classLoader.getResourceAsStream("chain/runtimeVersion.json").text
        when:
        def x = objectMapper.readValue(json, RuntimeVersionJson)
        def y = objectMapper.readValue(json, RuntimeVersionJson)
        then:
        x == y
        x.hashCode() == y.hashCode()
    }

    def "Diff are not equal"() {
        setup:
        String json = BlockJsonSpec.classLoader.getResourceAsStream("chain/runtimeVersion.json").text
        when:
        def x = objectMapper.readValue(json, RuntimeVersionJson)
        def y = objectMapper.readValue(json, RuntimeVersionJson).tap {
            specVersion++
        }
        then:
        x != y
    }

}
