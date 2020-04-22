package io.emeraldpay.pjc.ss58

import spock.lang.Specification

class AddressTypeSpec extends Specification {

    def "Cannot create non-byte-size custom type"() {
        when:
        new AddressType.Custom(1000)
        then:
        thrown(IllegalArgumentException)
    }

    def "Cannot create reserved custom type"() {
        when:
        new AddressType.Custom(64)
        then:
        thrown(IllegalArgumentException)
    }

    def "Create custom from int"() {
        when:
        def act = new AddressType.Custom(42)
        then:
        act.value == 42.byteValue()
    }

    def "Create custom from non-byte int"() {
        when:
        def act = new AddressType.Custom(42)
        then:
        act.value == 42.byteValue()
    }

    def "Create custom from byte"() {
        when:
        byte b = 0b00101010
        def act = new AddressType.Custom(b)
        then:
        act.value == 42.byteValue()
    }
}
