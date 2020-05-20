package io.emeraldpay.pjc.json.jackson

import com.fasterxml.jackson.core.JsonGenerationException
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import io.emeraldpay.pjc.json.JsonSpecCommons
import spock.lang.Specification

class HexLongSerializerSpec extends Specification {

    def "Serialize null"() {
        setup:
        def ser = new HexLongSerializer()
        def gen = Mock(JsonGenerator)
        when:
        ser.serialize(null, gen, Stub(SerializerProvider))
        then:
        1 * gen.writeNull()
    }

    def "Serialize value"() {
        setup:
        def ser = new HexLongSerializer()
        expect:
        def buf = new ByteArrayOutputStream()
        def gen = JsonSpecCommons.objectMapper.createGenerator(buf)
        ser.serialize(value, gen, Stub(SerializerProvider))
        gen.close()
        new String(buf.toByteArray()) == exp
        where:
        value   | exp
        0       | '"0x0"'
        1       | '"0x1"'
        0xf     | '"0xf"'
        0xff    | '"0xff"'
        Long.MAX_VALUE    | '"0x7fffffffffffffff"'
    }

    def "Cannot serialize negative value"() {
        setup:
        def ser = new HexLongSerializer()
        when:
        def buf = new ByteArrayOutputStream()
        def gen = JsonSpecCommons.objectMapper.createGenerator(buf)
        ser.serialize(-1, gen, Stub(SerializerProvider))
        then:
        thrown(JsonGenerationException)
    }
}
