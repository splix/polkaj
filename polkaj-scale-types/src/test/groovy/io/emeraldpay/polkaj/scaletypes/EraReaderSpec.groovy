package io.emeraldpay.polkaj.scaletypes

import io.emeraldpay.polkaj.scale.ScaleCodecReader
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class EraReaderSpec extends Specification {

    def reader = new EraReader()

    def "Reads immortal era"() {
        setup:
        def codec = new ScaleCodecReader(Hex.decodeHex("00"))
        when:
        def act = codec.read(reader)
        then:
        act == 0
        !codec.hasNext()
    }

    def "Reads mortal era"() {
        setup:
        def codec = new ScaleCodecReader(Hex.decodeHex("e500"))
        when:
        def act = codec.read(reader)
        then:
        act == 229
        !codec.hasNext()
    }

    def "Reads all cases"() {
        expect:
        def codec = new ScaleCodecReader(Hex.decodeHex(encoded))
        codec.hasNext()
        codec.read(reader) == value
        !codec.hasNext()
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

    def "Errors if no input"() {
        setup:
        def codec = new ScaleCodecReader(Hex.decodeHex(""))
        when:
        def act = codec.read(reader)
        then:
        thrown(IndexOutOfBoundsException)
    }
}
