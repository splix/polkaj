package io.emeraldpay.pjc.types

import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class FixedBytesSpec extends Specification {

    def "Cannot compare different sizes"() {
        setup:
        def value1 = new FixedBytes(Hex.decodeHex("00"), 1) {}
        def value2 = new FixedBytes(Hex.decodeHex("0000"), 2) {}

        when:
        value1.compareTo(value2)

        then:
        def t = thrown(IllegalStateException)
        t.message.contains("Different size")
    }


}
