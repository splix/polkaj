package io.emeraldpay.pjc.scale

import spock.lang.Specification

class CompactModeSpec extends Specification {

    def "Get mode to store Integer"() {
        expect:
        CompactMode.forNumber(number) == mode
        where:
        mode                    | number
        CompactMode.SINGLE      | 0
        CompactMode.SINGLE      | 1
        CompactMode.SINGLE      | 10
        CompactMode.SINGLE      | 63
        CompactMode.SINGLE      | 0x3f

        CompactMode.TWO         | 64
        CompactMode.TWO         | 100
        CompactMode.TWO         | 1000
        CompactMode.TWO         | 10000
        CompactMode.TWO         | (2**14)-1  //16383
        CompactMode.TWO         | 0x3f_ff //16383

        CompactMode.FOUR        | 0x40_00
        CompactMode.FOUR        | 2**14
        CompactMode.FOUR        | 100000
        CompactMode.FOUR        | 1000000
        CompactMode.FOUR        | (2**30)-1
        CompactMode.FOUR        | 0x3f_ff_ff_ff

        CompactMode.BIGINT      | 0x40_00_00_00
        CompactMode.BIGINT      | 2**30
        CompactMode.BIGINT      | (2**30) + 1
        CompactMode.BIGINT      | Integer.MAX_VALUE //0x7f_ff_ff_ff
    }

    def "Get mode to store Long"() {
        expect:
        CompactMode.forNumber(number) == mode
        where:
        mode                    | number
        CompactMode.SINGLE      | 0L
        CompactMode.SINGLE      | 1L
        CompactMode.SINGLE      | 10L
        CompactMode.SINGLE      | 63L
        CompactMode.SINGLE      | 0x3fL

        CompactMode.TWO         | 64L
        CompactMode.TWO         | 100L
        CompactMode.TWO         | 1000L
        CompactMode.TWO         | 10000L
        CompactMode.TWO         | (2L**14)-1  //16383
        CompactMode.TWO         | 0x3f_ffL //16383

        CompactMode.FOUR        | 0x40_00L
        CompactMode.FOUR        | 2L**14
        CompactMode.FOUR        | 100000L
        CompactMode.FOUR        | 1000000L
        CompactMode.FOUR        | (2L**30)-1
        CompactMode.FOUR        | 0x3f_ff_ff_ffL

        CompactMode.BIGINT      | 0x40_00_00_00L
        CompactMode.BIGINT      | 2L**30
        CompactMode.BIGINT      | (2L**30) + 1
        CompactMode.BIGINT      | 0xff_ff_ff_ffL
        CompactMode.BIGINT      | Long.MAX_VALUE
    }

    def "Get mode to store BigInteger"() {
        expect:
        CompactMode.forNumber(number) == mode
        where:
        mode                    | number
        CompactMode.SINGLE      | BigInteger.valueOf(0)
        CompactMode.SINGLE      | BigInteger.valueOf(1)
        CompactMode.SINGLE      | BigInteger.valueOf(10)
        CompactMode.SINGLE      | BigInteger.valueOf(63)
        CompactMode.SINGLE      | BigInteger.valueOf(0x3f)

        CompactMode.TWO         | BigInteger.valueOf(64)
        CompactMode.TWO         | BigInteger.valueOf(100)
        CompactMode.TWO         | BigInteger.valueOf(1000)
        CompactMode.TWO         | BigInteger.valueOf(10000)
        CompactMode.TWO         | BigInteger.valueOf((2**14)-1)  //16383
        CompactMode.TWO         | BigInteger.valueOf(0x3f_ff) //16383

        CompactMode.FOUR        | BigInteger.valueOf(0x40_00)
        CompactMode.FOUR        | BigInteger.valueOf(2**14)
        CompactMode.FOUR        | BigInteger.valueOf(100000)
        CompactMode.FOUR        | BigInteger.valueOf(1000000)
        CompactMode.FOUR        | BigInteger.valueOf((2**30)-1)
        CompactMode.FOUR        | BigInteger.valueOf(0x3f_ff_ff_ff)

        CompactMode.BIGINT      | BigInteger.valueOf(0x40_00_00_00)
        CompactMode.BIGINT      | BigInteger.valueOf(2**30)
        CompactMode.BIGINT      | BigInteger.valueOf((2**30) + 1)
        CompactMode.BIGINT      | BigInteger.valueOf(0xff_ff_ff_ffL)
        CompactMode.BIGINT      | BigInteger.valueOf(Long.MAX_VALUE)
        CompactMode.BIGINT      | new BigInteger("12345678901234567890")
    }

    def "No mode for negative number"() {
        when:
        CompactMode.forNumber(-1)
        then:
        thrown(IllegalArgumentException)

        when:
        CompactMode.forNumber(-1L)
        then:
        thrown(IllegalArgumentException)

        when:
        CompactMode.forNumber(BigInteger.valueOf(-1))
        then:
        thrown(IllegalArgumentException)
    }

    def "No mode 536 bit int"() {
        setup:
        String hex = "ff" * 536
        when:
        CompactMode.forNumber(new BigInteger(hex, 16))
        then:
        thrown(IllegalArgumentException)
    }

}
