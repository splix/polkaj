package io.emeraldpay.polkaj.types

import spock.lang.Specification

class ByteDataSpec extends Specification {

    def "Create"() {
        when:
        def act = ByteData.from("0x0102")
        then:
        act.bytes == [1, 2] as byte[]
    }

    def "Create empty from empty string"() {
        when:
        def act = ByteData.from("")
        then:
        act.bytes.size() == 0
    }

    def "Create empty from 0x string"() {
        when:
        def act = ByteData.from("0x")
        then:
        act.bytes.size() == 0
    }

    def "Cannot create from null"() {
        when:
        ByteData.from(null)
        then:
        thrown(NullPointerException)
    }

    def "Cannot create from invalid"() {
        when:
        ByteData.from("hello")
        then:
        thrown(NumberFormatException)

        when:
        ByteData.from("1010M")
        then:
        thrown(NumberFormatException)
    }

    def "Cannot create from non-even"() {
        when:
        ByteData.from("f")
        then:
        thrown(NumberFormatException)

        when:
        ByteData.from("123")
        then:
        thrown(NumberFormatException)
    }

    def "Same are equal"() {
        when:
        def x = ByteData.from("0x12345678")
        def y = ByteData.from("0x12345678")
        then:
        x == y
        x.hashCode() == y.hashCode()
    }

    def "Diff are not equal"() {
        when:
        def x = ByteData.from("0x12345678")
        def y = ByteData.from("0x1234567f")
        then:
        x != y
    }

}
