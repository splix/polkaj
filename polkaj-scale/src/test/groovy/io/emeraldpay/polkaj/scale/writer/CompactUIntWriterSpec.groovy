package io.emeraldpay.polkaj.scale.writer

import io.emeraldpay.polkaj.scale.ScaleCodecWriter
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class CompactUIntWriterSpec extends Specification {

    CompactUIntWriter writer = new CompactUIntWriter()
    ByteArrayOutputStream buf = new ByteArrayOutputStream()
    ScaleCodecWriter codec = new ScaleCodecWriter(buf)

    def "Writes single byte int"() {
        expect:
        codec.write(writer, value)
        Hex.encodeHexString(buf.toByteArray()) == encoded

        where:
        encoded         | value
        "00"            | 0
        "04"            | 1
        "a8"            | 42
        "fc"            | 63
    }

    def "Writes two byte int"() {
        expect:
        codec.write(writer, value)
        Hex.encodeHexString(buf.toByteArray()) == encoded

        where:
        encoded         | value
        "0101"          | 64
        "1501"          | 69
        "fdff"          | 16383
    }

    def "Writes four byte int"() {
        expect:
        codec.write(writer, value)
        Hex.encodeHexString(buf.toByteArray()) == encoded

        where:
        encoded         | value
        "02000100"      | 16384
        "feff0700"      | 0x01_ff_ff
        "feffffff"      | 0x3f_ff_ff_ff
    }

    def "Writes more than four byte int"() {
        expect:
        codec.write(writer, value)
        Hex.encodeHexString(buf.toByteArray()) == encoded

        where:
        encoded         | value
        "0300000040"    | 0x40_00_00_00
        "0370605040"    | 0x40_50_60_70
    }
}
