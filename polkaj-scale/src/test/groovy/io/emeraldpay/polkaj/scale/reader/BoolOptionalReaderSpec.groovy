package io.emeraldpay.polkaj.scale.reader

import io.emeraldpay.polkaj.scale.ScaleCodecReader
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class BoolOptionalReaderSpec extends Specification {

    def reader = new BoolOptionalReader()

    def "Reads existing true bool"() {
        setup:
        def codec = new ScaleCodecReader(Hex.decodeHex("02"))
        when:
        def act = codec.read(reader)
        then:
        act.isPresent()
        act.get() == true
        !codec.hasNext()
    }

    def "Reads existing false bool"() {
        setup:
        def codec = new ScaleCodecReader(Hex.decodeHex("01"))
        when:
        def act = codec.read(reader)
        then:
        act.isPresent()
        act.get() == false
        !codec.hasNext()
    }

    def "Reads no bool"() {
        setup:
        def codec = new ScaleCodecReader(Hex.decodeHex("00"))
        when:
        def act = codec.read(reader)
        then:
        !act.isPresent()
        !codec.hasNext()
    }

    def "Errors if no input"() {
        setup:
        def codec = new ScaleCodecReader(Hex.decodeHex(""))
        when:
        def act = codec.read(reader)
        then:
        thrown(IndexOutOfBoundsException)
    }

    def "Errors if invalid value"() {
        setup:
        def codec = new ScaleCodecReader(Hex.decodeHex("03"))
        when:
        def act = codec.read(reader)
        then:
        thrown(IllegalStateException)
    }

    def "Reads bool through codec optional method"() {
        expect:
        new ScaleCodecReader(Hex.decodeHex(input)).readOptional(reader) == value
        where:
        input   | value
        "00"    | Optional<Boolean>.empty()
        "01"    | Optional.of(false)
        "02"    | Optional.of(true)
    }

}
