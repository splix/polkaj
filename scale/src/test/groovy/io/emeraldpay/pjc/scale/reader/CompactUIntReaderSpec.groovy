package io.emeraldpay.pjc.scale.reader

import io.emeraldpay.pjc.scale.ScaleCodecReader
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class CompactUIntReaderSpec extends Specification {

    def reader = new CompactUIntReader()

    def "Reads single byte int"() {
        expect:
        def codec = new ScaleCodecReader(Hex.decodeHex(encoded))
        codec.hasNext()
        codec.read(reader) == value
        !codec.hasNext()

        where:
        encoded         | value
        "00"            | 0
        "04"            | 1
        "a8"            | 42
        "fc"            | 63
    }

    def "Reads two byte int"() {
        expect:
        def codec = new ScaleCodecReader(Hex.decodeHex(encoded))
        codec.hasNext()
        codec.read(reader) == value
        !codec.hasNext()

        where:
        encoded         | value
        "0101"          | 64
        "1501"          | 69
        "fdff"          | 16383
    }

    def "Reads four byte int"() {
        expect:
        def codec = new ScaleCodecReader(Hex.decodeHex(encoded))
        codec.hasNext()
        codec.read(reader) == value
        !codec.hasNext()

        where:
        encoded         | value
        "02000100"      | 16384
        "feffffff"      | 0x3f_ff_ff_ff
    }

    def "Errors to reads bigint"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("33aabbccddeeff00112233445566778899"))
        codec.read(reader)
        then:
        thrown(UnsupportedOperationException)
    }
}
