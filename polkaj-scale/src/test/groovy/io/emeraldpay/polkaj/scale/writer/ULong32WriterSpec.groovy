package io.emeraldpay.polkaj.scale.writer

import io.emeraldpay.polkaj.scale.ScaleCodecWriter
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class ULong32WriterSpec extends Specification {

    ULong32Writer writer = new ULong32Writer()
    ByteArrayOutputStream buf = new ByteArrayOutputStream()
    ScaleCodecWriter codec = new ScaleCodecWriter(buf)

    def "Writes"() {
        when:
        codec.write(writer, 16777215L)
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "ffffff00"
    }

    def "Writes optional existing"() {
        when:
        codec.writeOptional(writer, 16777215L)
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "01ffffff00"
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
        codec.writeOptional(writer, (Long)null)
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
        encoded     | value
        "00000000"  | 0x00_00_00_00L
        "000000ff"  | 0xff_00_00_00L
        "0000ff00"  | 0x00_ff_00_00L
        "00ff0000"  | 0x00_00_ff_00L
        "ff000000"  | 0x00_00_00_ffL
        "0f0f0f0f"  | 0x0f_0f_0f_0fL
        "f0f0f0f0"  | 0xf0_f0_f0_f0L

        "ffffff00"  | 0x00_ff_ff_ffL
        "00060000"  | 0x00_00_06_00L
        "00030000"  | 0x00_00_03_00L
        "7d010000"  | 0x00_00_01_7dL
    }

    def "Error for negative number"() {
        when:
        codec.write(writer, -1L)
        then:
        thrown(IllegalArgumentException)
    }

    def "Error for large number"() {
        when:
        codec.write(writer, Long.MAX_VALUE)
        then:
        thrown(IllegalArgumentException)

        when:
        codec.write(writer, 0x01_00_00_00_00L)
        then:
        thrown(IllegalArgumentException)
    }
}
