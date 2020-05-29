package io.emeraldpay.polkaj.json.jackson

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import spock.lang.Specification

class DeserializeCommonsSpec extends Specification {

    def "Return null hex for empty or null input"() {
        when:
        def act = DeserializeCommons.getHexString((JsonNode)null)
        then:
        act == null

        when:
        act = DeserializeCommons.getHexString((String)null)
        then:
        act == null

        when:
        act = DeserializeCommons.getHexString("")
        then:
        act == null

        when:
        act = DeserializeCommons.getHexString("0x")
        then:
        act == null
    }

    def "Return hex value"() {
        when:
        def act = DeserializeCommons.getHexString(TextNode.valueOf("0xff"))
        then:
        act == "0xff"

        when:
        act = DeserializeCommons.getHexString("0xff")
        then:
        act == "0xff"
    }
}
