package io.emeraldpay.polkaj.json.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import io.emeraldpay.polkaj.types.Address
import spock.lang.Specification

class AddressSerializerSpec extends Specification {

    def "Serialize null"() {
        setup:
        def ser = new AddressSerializer()
        def gen = Mock(JsonGenerator)
        when:
        ser.serialize(null, gen, Stub(SerializerProvider))
        then:
        1 * gen.writeNull()
    }

    def "Serialize value"() {
        setup:
        def ser = new AddressSerializer()
        def gen = Mock(JsonGenerator)
        when:
        ser.serialize(Address.from("5HgsbKKAqD82bDv25MakEihbS4DXKCdyM76HQFRZYmMdYLcJ"), gen, Stub(SerializerProvider))
        then:
        1 * gen.writeString("5HgsbKKAqD82bDv25MakEihbS4DXKCdyM76HQFRZYmMdYLcJ")
    }
}
