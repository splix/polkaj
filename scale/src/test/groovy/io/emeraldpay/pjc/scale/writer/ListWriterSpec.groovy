package io.emeraldpay.pjc.scale.writer

import io.emeraldpay.pjc.scale.ScaleCodecWriter
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class ListWriterSpec extends Specification {

    ByteArrayOutputStream buf = new ByteArrayOutputStream()
    ScaleCodecWriter codec = new ScaleCodecWriter(buf)

    def "Writes empty list"() {
        setup:
        ListWriter writer = new ListWriter(new BoolWriter())
        when:
        codec.write(writer,[])
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "00"
    }

    def "Writes unsigned 16-bit integers"() {
        setup:
        ListWriter writer = new ListWriter(new UInt16Writer())
        when:
        codec.write(writer,[4, 8, 15, 16, 23, 42])
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "18040008000f00100017002a00"
    }
}
