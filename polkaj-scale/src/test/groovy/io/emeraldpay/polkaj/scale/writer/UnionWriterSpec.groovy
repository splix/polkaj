package io.emeraldpay.polkaj.scale.writer

import io.emeraldpay.polkaj.scale.ScaleCodecWriter
import io.emeraldpay.polkaj.scale.UnionValue
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class UnionWriterSpec extends Specification {

    ByteArrayOutputStream buf = new ByteArrayOutputStream()
    ScaleCodecWriter codec = new ScaleCodecWriter(buf)

    def "Writes Int for IntOrBool"() {
        setup:
        UnionWriter<Object> writer = new UnionWriter<>(
                new UByteWriter(), new BoolWriter()
        )
        when:
        codec.write(writer, new UnionValue<>(0, 42))
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "002a"
    }

    def "Writes Bpp; for IntOrBool"() {
        setup:
        UnionWriter<Object> writer = new UnionWriter<>(
                new UByteWriter(), new BoolWriter()
        )
        when:
        codec.write(writer, new UnionValue<>(1, true))
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "0101"
    }
}
