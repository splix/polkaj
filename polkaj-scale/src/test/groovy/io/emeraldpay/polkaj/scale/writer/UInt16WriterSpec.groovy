package io.emeraldpay.polkaj.scale.writer

import io.emeraldpay.polkaj.scale.ScaleCodecWriter
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class UInt16WriterSpec extends Specification {

    UInt16Writer writer = new UInt16Writer()
    ByteArrayOutputStream buf = new ByteArrayOutputStream()
    ScaleCodecWriter codec = new ScaleCodecWriter(buf)

    def "Writes"() {
        when:
        codec.write(writer, 42)
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "2a00"
    }

    def "Writes optional existing"() {
        when:
        codec.writeOptional(writer, 42)
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "012a00"
    }

    def "Writes optional existing as object"() {
        when:
        codec.writeOptional(writer, Optional.of(42))
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "012a00"
    }

    def "Writes optional empty"() {
        when:
        codec.writeOptional(writer, Optional.empty())
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "00"
    }

    def "Writes optional null"() {
        when:
        codec.writeOptional(writer, (Integer)null)
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "00"
    }

    def "Writes all cases"() {
        expect:
        ByteArrayOutputStream buf = new ByteArrayOutputStream()
        new ScaleCodecWriter(buf).write(writer, value)
        Hex.encodeHexString(buf.toByteArray()) == encoded
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
