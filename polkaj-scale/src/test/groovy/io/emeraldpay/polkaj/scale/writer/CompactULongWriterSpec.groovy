package io.emeraldpay.polkaj.scale.writer

import io.emeraldpay.polkaj.scale.ScaleCodecWriter
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class CompactULongWriterSpec extends Specification {

    CompactULongWriter writer = new CompactULongWriter()
    ByteArrayOutputStream buf = new ByteArrayOutputStream()
    ScaleCodecWriter codec = new ScaleCodecWriter(buf)

    def "Writes single byte"() {
        expect:
        codec.write(writer, value)
        Hex.encodeHexString(buf.toByteArray()) == encoded

        where:
        encoded         | value
        "00"            | 0L
        "04"            | 1L
        "a8"            | 42L
        "fc"            | 63L
    }

    def "Writes two byte"() {
        expect:
        codec.write(writer, value)
        Hex.encodeHexString(buf.toByteArray()) == encoded

        where:
        encoded         | value
        "0101"          | 64L
        "1501"          | 69L
        "fdff"          | 16383L
    }

    def "Writes four byte"() {
        expect:
        codec.write(writer, value)
        Hex.encodeHexString(buf.toByteArray()) == encoded

        where:
        encoded         | value
        "02000100"      | 16384L
        "feff0700"      | 0x01_ff_ffL
        "feffffff"      | 0x3f_ff_ff_ffL
    }

    def "Writes more than four byte int"() {
        expect:
        codec.write(writer, value)
        Hex.encodeHexString(buf.toByteArray()) == encoded

        where:
        encoded              | value
        "0300000040"         | 0x40_00_00_00L
        "0370605040"         | 0x40_50_60_70L
        "13ffffffffffffff7f" | Long.MAX_VALUE
        "13feffffffffffff7f" | Long.MAX_VALUE - 1
    }
}
