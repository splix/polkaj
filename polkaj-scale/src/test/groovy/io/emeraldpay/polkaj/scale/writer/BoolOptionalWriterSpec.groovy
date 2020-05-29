package io.emeraldpay.polkaj.scale.writer

import io.emeraldpay.polkaj.scale.ScaleCodecWriter
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class BoolOptionalWriterSpec extends Specification {

    BoolOptionalWriter writer = new BoolOptionalWriter()
    ByteArrayOutputStream buf = new ByteArrayOutputStream()
    ScaleCodecWriter codec = new ScaleCodecWriter(buf)

    def "Writes true"() {
        when:
        codec.write(writer, Optional.of(true))
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "02"
    }

    def "Writes false"() {
        when:
        codec.write(writer, Optional.of(false))
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "01"
    }

    def "Writes empty"() {
        when:
        codec.write(writer, Optional.empty())
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "00"
    }

    def "Writes true through optional method"() {
        when:
        codec.writeOptional(writer, Optional.of(true))
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "02"
    }

    def "Writes false through optional method"() {
        when:
        codec.write(writer, Optional.of(false))
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "01"
    }

    def "Writes empty through optional method"() {
        when:
        codec.write(writer, Optional.empty())
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "00"
    }

}
