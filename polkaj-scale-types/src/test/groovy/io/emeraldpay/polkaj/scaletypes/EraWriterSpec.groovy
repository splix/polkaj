package io.emeraldpay.polkaj.scaletypes

import io.emeraldpay.polkaj.scale.ScaleCodecWriter
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class EraWriterSpec extends Specification {

    def writer = new EraWriter()
    def buf = new ByteArrayOutputStream()
    def codec = new ScaleCodecWriter(buf)

    def "Writes immortal era"() {
        when:
        codec.write(writer, 0)
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "00"
    }

    def "Writes mortal era"() {
        when:
        codec.write(writer, 229)
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "e500"
    }

    def "Writes all cases"() {
        expect:
        def buf = new ByteArrayOutputStream()
        new ScaleCodecWriter(buf).write(writer, value)
        Hex.encodeHexString(buf.toByteArray()) == encoded
        where:
        encoded         | value
        "00"            | 0
        "0400"          | 4
        "3200"          | 50
        "3502"          | 565
        "db00"          | 219
        "e500"          | 229
        "eb00"          | 235
        "f501"          | 501
        "fb00"          | 251
    }
}
