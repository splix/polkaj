package io.emeraldpay.pjc.types

import spock.lang.Specification

class HexNumberFormatSpec extends Specification {

    def "Parse Integer"() {
        expect:
        HexNumberFormat.parseInt(hex) == act
        where:
        hex             | act
        null            | null
        "0x"            | null
        "0x0"           | 0
        "0x1"           | 1
        "0x0f"          | 15
        "0xff"          | 0xff
        "0x00ff"        | 0xff
        "0x1234ab"      | 0x1234ab
        "0x7fffffff"    | Integer.MAX_VALUE
    }

    def "Format Integer"() {
        expect:
        Integer value = act
        HexNumberFormat.toString(value) == hex
        where:
        hex             | act
        "0x0"           | 0
        "0x1"           | 1
        "0xf"           | 15
        "0xff"          | 0xff
        "0x1234ab"      | 0x1234ab
        "0x7fffffff"    | Integer.MAX_VALUE
    }

    def "Parse Long"() {
        expect:
        HexNumberFormat.parseLong(hex) == act
        where:
        hex                     | act
        null                    | null
        "0x"                    | null
        "0x0"                   | 0L
        "0x1"                   | 1L
        "0x0f"                  | 15L
        "0xff"                  | 0xffL
        "0x00ff"                | 0xffL
        "0x1234ab"              | 0x1234abL
        "0x7fffffff"            | 0x7fffffffL
        "0xffffffff"            | 0xffffffffL
        "0x7fffffffffffffff"    | Long.MAX_VALUE
    }

    def "Format Long"() {
        expect:
        Long value = act
        HexNumberFormat.toString(value) == hex
        where:
        hex                     | act
        "0x0"                   | 0L
        "0x1"                   | 1L
        "0xff"                  | 0xffL
        "0x1234ab"              | 0x1234abL
        "0x7fffffff"            | 0x7fffffffL
        "0xffffffff"            | 0xffffffffL
        "0x7fffffffffffffff"    | Long.MAX_VALUE
    }

    def "Parse BigInteger"() {
        expect:
        HexNumberFormat.parseBigInt(hex) == act
        where:
        hex                     | act
        null                    | null
        "0x"                    | null
        "0x0"                   | BigInteger.ZERO
        "0x1"                   | BigInteger.valueOf(1)
        "0x0f"                  | BigInteger.valueOf(15L)
        "0x000000007fffffff"    | BigInteger.valueOf(0x7fffffffL)
        "0x7fffffffffffffff"    | BigInteger.valueOf(Long.MAX_VALUE)
        "0xffffffffffffffff"    | new BigInteger("ffffffffffffffff", 16)
    }

    def "Format BigInteger"() {
        expect:
        HexNumberFormat.toString(act) == hex
        where:
        hex                     | act
        "0x0"                   | BigInteger.ZERO
        "0x1"                   | BigInteger.valueOf(1)
        "0xf"                   | BigInteger.valueOf(15L)
        "0x7fffffff"            | BigInteger.valueOf(0x7fffffffL)
        "0x7fffffffffffffff"    | BigInteger.valueOf(Long.MAX_VALUE)
        "0xffffffffffffffff"    | new BigInteger("ffffffffffffffff", 16)
    }

    def "Format null"() {
        when:
        def act = HexNumberFormat.toString((Integer)null)
        then:
        act == "0x"

        when:
        act = HexNumberFormat.toString((Long)null)
        then:
        act == "0x"

        when:
        act = HexNumberFormat.toString((BigInteger)null)
        then:
        act == "0x"
    }

    def "Doesn't format negative numbers"() {
        when:
        HexNumberFormat.toString(-1)
        then:
        thrown(IllegalArgumentException)

        when:
        HexNumberFormat.toString(-1L)
        then:
        thrown(IllegalArgumentException)

        when:
        HexNumberFormat.toString(BigInteger.valueOf(-1))
        then:
        thrown(IllegalArgumentException)
    }

    def "Doesn't accept without prefix"() {
        when:
        HexNumberFormat.clean("1")
        then:
        thrown(IllegalArgumentException)
    }

    def "Doesn't accept negative"() {
        when:
        HexNumberFormat.clean("0x-1")
        then:
        thrown(IllegalArgumentException)

        when:
        HexNumberFormat.clean("-0x1")
        then:
        thrown(IllegalArgumentException)
    }
}
