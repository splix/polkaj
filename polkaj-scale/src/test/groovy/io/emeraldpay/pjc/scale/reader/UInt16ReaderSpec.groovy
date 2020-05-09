package io.emeraldpay.pjc.scale.reader

import io.emeraldpay.pjc.scale.ScaleCodecReader
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class UInt16ReaderSpec extends Specification {

    UInt16Reader reader = new UInt16Reader()

    def "Reads"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("2a00"))
        then:
        codec.hasNext()
        codec.read(reader) == 42
        !codec.hasNext()
    }

    def "Error for short"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("ff"))
        codec.read(reader)
        then:
        thrown(IndexOutOfBoundsException)
    }

    def "Reads optional existing"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("012a00"))
        then:
        codec.hasNext()
        codec.readOptional(reader) == Optional.of(42)
        !codec.hasNext()
    }

    def "Reads optional none"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("00"))
        then:
        codec.hasNext()
        codec.readOptional(reader) == Optional.empty()
        !codec.hasNext()
    }

    def "Reads all cases"() {
        expect:
        new ScaleCodecReader(Hex.decodeHex(encoded)).read(reader) == value
        where:
        encoded | value
        "0000"  | 0x00_00
        "00ff"  | 0xff_00
        "ff00"  | 0x00_ff
        "ffff"  | 0xff_ff
        "f0f0"  | 0xf0_f0
        "0f0f"  | 0x0f_0f
        "f00f"  | 0x0f_f0
    }
}
