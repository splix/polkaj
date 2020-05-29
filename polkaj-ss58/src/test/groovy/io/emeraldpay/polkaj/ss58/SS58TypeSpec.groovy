package io.emeraldpay.polkaj.ss58

import spock.lang.Specification

class SS58TypeSpec extends Specification {

    def "Cannot create non-byte-size custom type"() {
        when:
        new SS58Type.Custom(1000)
        then:
        thrown(IllegalArgumentException)
    }

    def "Cannot create reserved custom type"() {
        when:
        new SS58Type.Custom(64)
        then:
        thrown(IllegalArgumentException)
    }

    def "Create custom from int"() {
        when:
        def act = new SS58Type.Custom(42)
        then:
        act.value == 42.byteValue()
    }

    def "Create custom from non-byte int"() {
        when:
        def act = new SS58Type.Custom(42)
        then:
        act.value == 42.byteValue()
    }

    def "Create custom from byte"() {
        when:
        byte b = 0b00101010
        def act = new SS58Type.Custom(b)
        then:
        act.value == 42.byteValue()
    }
}
