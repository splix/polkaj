package io.emeraldpay.pjc.scale.reader

import io.emeraldpay.pjc.scale.ScaleCodecReader
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class BoolReaderSpec extends Specification {

    def reader = new BoolReader()

    def "Reads true bool"() {
        setup:
        def codec = new ScaleCodecReader(Hex.decodeHex("01"))
        when:
        def act = codec.read(reader)
        then:
        act == true
        !codec.hasNext()
    }

    def "Reads false bool"() {
        setup:
        def codec = new ScaleCodecReader(Hex.decodeHex("00"))
        when:
        def act = codec.read(reader)
        then:
        act == false
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
        def codec = new ScaleCodecReader(Hex.decodeHex("02"))
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
