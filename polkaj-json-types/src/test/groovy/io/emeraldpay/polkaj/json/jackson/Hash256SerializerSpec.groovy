package io.emeraldpay.polkaj.json.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import io.emeraldpay.polkaj.types.Hash256
import spock.lang.Specification

class Hash256SerializerSpec extends Specification {

    def "Serialize null"() {
        setup:
        def ser = new Hash256Serializer()
        def gen = Mock(JsonGenerator)
        when:
        ser.serialize(null, gen, Stub(SerializerProvider))
        then:
        1 * gen.writeNull()
    }

    def "Serialize value"() {
        setup:
        def ser = new Hash256Serializer()
        def gen = Mock(JsonGenerator)
        when:
        ser.serialize(Hash256.from("0x9623f79d8bd2248c2777f88e2e5ee9063b1a2991cfab15a97c11f7f89d6e97e9"), gen, Stub(SerializerProvider))
        then:
        1 * gen.writeString("0x9623f79d8bd2248c2777f88e2e5ee9063b1a2991cfab15a97c11f7f89d6e97e9")
    }

    def "Serialize zero value"() {
        setup:
        def ser = new Hash256Serializer()
        def gen = Mock(JsonGenerator)
        when:
        ser.serialize(Hash256.empty(), gen, Stub(SerializerProvider))
        then:
        1 * gen.writeString("0x0000000000000000000000000000000000000000000000000000000000000000")
    }
}
