package io.emeraldpay.pjc.scale.reader

import io.emeraldpay.pjc.scale.ScaleCodecReader
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class UnionReaderSpec extends Specification {

    UnionReader<Object> reader = new UnionReader<>(
            new UByteReader(), new BoolReader()
    )

    def "Read Int-Bool"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("002a"))
        def act = codec.read(reader)
        then:
        act.index == 0
        act.value instanceof Integer
        act.value == 42

        when:
        codec = new ScaleCodecReader(Hex.decodeHex("0101"))
        act = codec.read(reader)
        then:
        act.index == 1
        act.value instanceof Boolean
        act.value == true
    }

    def "Error for invalid enum"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("032a"))
        def act = codec.read(reader)
        then:
        thrown(IllegalStateException)
    }

    def "Reads optional enum"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("01002a"))
        def act = codec.readOptional(reader)
        then:
        with(act.get()) {
            index == 0
            value == 42
        }
    }
}
