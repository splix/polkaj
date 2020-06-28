package io.emeraldpay.polkaj.json.jackson

import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import io.emeraldpay.polkaj.json.JsonSpecCommons
import io.emeraldpay.polkaj.types.Address
import spock.lang.Specification

class AddressDeserializerSpec extends Specification {

    ObjectMapper objectMapper = JsonSpecCommons.objectMapper

    def "Read null"() {
        setup:
        def ser = new AddressDeserializer()
        def jp = objectMapper.createParser('null')
        when:
        def act = ser.deserialize(jp, Stub(DeserializationContext))
        then:
        act == null
    }

    def "Read value"() {
        setup:
        def ser = new AddressDeserializer()
        def jp = objectMapper.createParser('"5HgsbKKAqD82bDv25MakEihbS4DXKCdyM76HQFRZYmMdYLcJ"')
        when:
        def act = ser.deserialize(jp, Stub(DeserializationContext))
        then:
        act == Address.from("5HgsbKKAqD82bDv25MakEihbS4DXKCdyM76HQFRZYmMdYLcJ")
    }

    def "Fail to read invalid value"() {
        setup:
        def ser = new AddressDeserializer()
        def jp = objectMapper.createParser('"0x06904e97fa0633753c291e48a64bf6a4978a0d57"')
        when:
        ser.deserialize(jp, Stub(DeserializationContext))
        then:
        def t = thrown(InvalidFormatException)
        t.value == "0x06904e97fa0633753c291e48a64bf6a4978a0d57"
    }
}
