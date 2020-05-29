package io.emeraldpay.polkaj.scale.reader

import io.emeraldpay.polkaj.scale.ScaleCodecReader
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class CompactBigIntReaderSpec extends Specification {

    CompactBigIntReader reader = new CompactBigIntReader()

    def "Reads single byte int"() {
        expect:
        def codec = new ScaleCodecReader(Hex.decodeHex(encoded))
        codec.hasNext()
        codec.read(reader) == BigInteger.valueOf(value)
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
        codec.read(reader) == BigInteger.valueOf(value)
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
        codec.read(reader) == BigInteger.valueOf(value)
        !codec.hasNext()

        where:
        encoded         | value
        "02000100"      | 16384
        "feffffff"      | 0x3f_ff_ff_ff
    }

    def "Reads four byte bigint"() {
        expect:
        def codec = new ScaleCodecReader(Hex.decodeHex(encoded))
        codec.hasNext()
        codec.read(reader) == BigInteger.valueOf(value)
        !codec.hasNext()

        where:
        encoded         | value
        "0300000040"    | 0x40_00_00_00
    }

    def "Reads bigint"() {
        expect:
        def codec = new ScaleCodecReader(Hex.decodeHex(encoded))
        codec.hasNext()
        codec.read(reader).toString(16) == value
        !codec.hasNext()

        where:
        encoded                                 | value
        "33aabbccddeeff00112233445566778899"    | "99887766554433221100ffeeddccbbaa"
    }
}
