package io.emeraldpay.polkaj.json.jackson

import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import io.emeraldpay.polkaj.json.JsonSpecCommons
import spock.lang.Specification

class HexLongDeserializerSpec extends Specification {

    ObjectMapper objectMapper = JsonSpecCommons.objectMapper

    def "Read null"() {
        setup:
        def ser = new HexLongDeserializer()
        def jp = objectMapper.createParser('null')
        when:
        def act = ser.deserialize(jp, Stub(DeserializationContext))
        then:
        act == null
    }

    def "Read empty as null"() {
        setup:
        def ser = new HexLongDeserializer()
        def jp = objectMapper.createParser('"0x"')
        when:
        def act = ser.deserialize(jp, Stub(DeserializationContext))
        then:
        act == null
    }

    def "Read value"() {
        setup:
        def ser = new HexLongDeserializer()
        expect:
        def jp = objectMapper.createParser(value)
        ser.deserialize(jp, Stub(DeserializationContext)) == exp
        where:
        value        | exp
        '"0x0"'      | 0L
        '"0x1234ff"' | 0x1234ffL
        '"0x7fffffffffffffff"' | 0x7fffffffffffffffL
    }
}
