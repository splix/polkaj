package io.emeraldpay.polkaj.scale.writer

import io.emeraldpay.polkaj.scale.ScaleCodecWriter
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class BoolWriterSpec extends Specification {

    BoolWriter writer = new BoolWriter()
    ByteArrayOutputStream buf = new ByteArrayOutputStream()
    ScaleCodecWriter codec = new ScaleCodecWriter(buf)

    def "Writes true"() {
        when:
        codec.write(writer,true)
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "01"
    }

    def "Writes false"() {
        when:
        codec.write(writer,false)
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "00"
    }

    def "Writes true as optional"() {
        when:
        codec.writeOptional(writer, true)
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "02"
    }

    def "Writes false as optional"() {
        when:
        codec.writeOptional(writer, false)
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "01"
    }

    def "Writes null as optional"() {
        when:
        codec.writeOptional(writer, (Boolean)null)
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "00"
    }
}
