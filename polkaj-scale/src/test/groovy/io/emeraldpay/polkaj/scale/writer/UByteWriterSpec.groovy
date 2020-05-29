package io.emeraldpay.polkaj.scale.writer

import io.emeraldpay.polkaj.scale.ScaleCodecWriter
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class UByteWriterSpec extends Specification {

    UByteWriter writer = new UByteWriter()
    ByteArrayOutputStream buf = new ByteArrayOutputStream()
    ScaleCodecWriter codec = new ScaleCodecWriter(buf)

    def "Writes"() {
        when:
        codec.write(writer,42)
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "2a"
    }

    def "Writes multiple"() {
        when:
        codec.write(writer,42)
        codec.write(writer,01)
        codec.write(writer,00)
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "2a0100"
    }

    def "Error for negative number"() {
        when:
        codec.write(writer, -1)
        then:
        thrown(IllegalArgumentException)
    }

    def "Error for large number"() {
        when:
        codec.write(writer, 256)
        then:
        thrown(IllegalArgumentException)
    }

}
