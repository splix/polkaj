package io.emeraldpay.pjc.scale.writer

import io.emeraldpay.pjc.scale.ScaleCodecWriter
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class UInt32WriterSpec extends Specification {

    UInt32Writer writer = new UInt32Writer()
    ByteArrayOutputStream buf = new ByteArrayOutputStream()
    ScaleCodecWriter codec = new ScaleCodecWriter(buf)

    def "Writes"() {
        when:
        codec.write(writer, 16777215)
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "ffffff00"
    }

    def "Writes optional existing"() {
        when:
        codec.writeOptional(writer, 16777215)
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
        //MAX Integer is 0x3f_ff_ff_ff
        encoded     | value
        "00000000"  | 0x00_00_00_00
        "0000ff00"  | 0x00_ff_00_00
        "00ff0000"  | 0x00_00_ff_00
        "ff000000"  | 0x00_00_00_ff
        "0f0f0f0f"  | 0x0f_0f_0f_0f

        "ffffff00"  | 0x00_ff_ff_ff
        "00060000"  | 0x00_00_06_00
        "00030000"  | 0x00_00_03_00
        "7d010000"  | 0x00_00_01_7d
    }

    def "Error for negative number"() {
        when:
        codec.write(writer, -1)
        then:
        thrown(IllegalArgumentException)
    }

}
