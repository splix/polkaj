package io.emeraldpay.polkaj.json.jackson


import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import io.emeraldpay.polkaj.json.JsonSpecCommons
import io.emeraldpay.polkaj.types.Hash256
import spock.lang.Specification

class Hash256DeserializerSpec extends Specification {

    ObjectMapper objectMapper = JsonSpecCommons.objectMapper

    def "Read null"() {
        setup:
        def ser = new Hash256Deserializer()
        def jp = objectMapper.createParser('null')
        when:
        def act = ser.deserialize(jp, Stub(DeserializationContext))
        then:
        act == null
    }

    def "Read value"() {
        setup:
        def ser = new Hash256Deserializer()
        def jp = objectMapper.createParser('"0x9623f79d8bd2248c2777f88e2e5ee9063b1a2991cfab15a97c11f7f89d6e97e9"')
        when:
        def act = ser.deserialize(jp, Stub(DeserializationContext))
        then:
        act == Hash256.from("0x9623f79d8bd2248c2777f88e2e5ee9063b1a2991cfab15a97c11f7f89d6e97e9")
    }
}
