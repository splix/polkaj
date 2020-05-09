package io.emeraldpay.pjc.scale.writer

import io.emeraldpay.pjc.scale.ScaleCodecWriter
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class CompactBigIntWriterSpec extends Specification {

    CompactBigIntWriter writer = new CompactBigIntWriter()
    ByteArrayOutputStream buf = new ByteArrayOutputStream()
    ScaleCodecWriter codec = new ScaleCodecWriter(buf)

    def "Writes single byte"() {
        expect:
        codec.write(writer, BigInteger.valueOf(value))
        Hex.encodeHexString(buf.toByteArray()) == encoded

        where:
        encoded         | value
        "00"            | 0
        "04"            | 1
        "a8"            | 42
        "fc"            | 63
    }

    def "Writes two byte"() {
        expect:
        codec.write(writer, BigInteger.valueOf(value))
        Hex.encodeHexString(buf.toByteArray()) == encoded

        where:
        encoded         | value
        "0101"          | 64
        "1501"          | 69
        "fdff"          | 16383
    }

    def "Writes four bytes"() {
        expect:
        codec.write(writer, BigInteger.valueOf(value))
        Hex.encodeHexString(buf.toByteArray()) == encoded

        where:
        encoded         | value
        "02000100"      | 16384
        "feff0700"      | 0x01_ff_ff
        "feffffff"      | 0x3f_ff_ff_ff
    }

    def "Writes more than four byte"() {
        expect:
        codec.write(writer, new BigInteger(value.replaceAll('_', ''), 16))
        Hex.encodeHexString(buf.toByteArray()) == encoded

        where:
        encoded         | value
        "0300000040"    | "40_00_00_00"
        "0370605040"    | "40_50_60_70"
    }
}
