package io.emeraldpay.polkaj.json

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

class BlockResponseJsonSpec extends Specification {

    ObjectMapper objectMapper = JsonSpecCommons.objectMapper

    def "Deserialize"() {
        setup:
        InputStream json = BlockJsonSpec.classLoader.getResourceAsStream("blocks/0x401a1-full.json")
        when:
        def act = objectMapper.readValue(json, BlockResponseJson)
        then:
        act != null
        act.block != null
        act.justifications.length == 0
        with(act.block) {
            extrinsics.size() == 3
            with(header) {
                number == 0x401a1
            }
        }
    }

    def "Same are equal"() {
        setup:
        String json = BlockJsonSpec.classLoader.getResourceAsStream("blocks/0x401a1-full.json").text
        when:
        def act1 = objectMapper.readValue(json, BlockResponseJson)
        def act2 = objectMapper.readValue(json, BlockResponseJson)
        then:
        act1 == act2
        act1 == act1
        act1.hashCode() == act2.hashCode()
    }

    def "Diff are not equal"() {
        setup:
        String json = BlockJsonSpec.classLoader.getResourceAsStream("blocks/0x401a1-full.json").text
        when:
        def act1 = objectMapper.readValue(json, BlockResponseJson)
        def act2 = objectMapper.readValue(json, BlockResponseJson).tap {
            block.header.number = 1
        }
        then:
        act1 != act2
    }
}
