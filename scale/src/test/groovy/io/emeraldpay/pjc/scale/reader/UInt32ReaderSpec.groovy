package io.emeraldpay.pjc.scale.reader

import io.emeraldpay.pjc.scale.ScaleCodecReader
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class UInt32ReaderSpec extends Specification {

    UInt32Reader reader = new UInt32Reader()

    def "Reads"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("ffffff00"))
        then:
        codec.hasNext()
        codec.read(reader) == 16777215L
        !codec.hasNext()
    }

    def "Error for short"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("ffffff"))
        codec.read(reader)
        then:
        thrown(IndexOutOfBoundsException)
    }

    def "Reads optional existing"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("01ffffff00"))
        then:
        codec.hasNext()
        codec.readOptional(reader) == Optional.of(16777215L)
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
        encoded     | value
        "00000000"  | 0x00_00_00_00
        "000000ff"  | 0xff_00_00_00
        "0000ff00"  | 0x00_ff_00_00
        "00ff0000"  | 0x00_00_ff_00
        "ff000000"  | 0x00_00_00_ff
        "0f0f0f0f"  | 0x0f_0f_0f_0f
        "f0f0f0f0"  | 0xf0_f0_f0_f0

        "ffffff00"  | 0x00_ff_ff_ff
        "00060000"  | 0x00_00_06_00
        "00030000"  | 0x00_00_03_00
        "7d010000"  | 0x00_00_01_7d
    }
}
