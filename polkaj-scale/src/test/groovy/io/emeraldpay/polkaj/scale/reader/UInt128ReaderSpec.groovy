package io.emeraldpay.polkaj.scale.reader

import io.emeraldpay.polkaj.scale.ScaleCodecReader
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class UInt128ReaderSpec extends Specification {

    UInt128Reader reader = new UInt128Reader()

    def "Reads"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("f70af5f6f3c843050000000000000000"))
        then:
        codec.read(reader).toString() == "379367743775116023"
    }

    def "Reads with zero prefix"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("0000c52ebca2b1000000000000000000"))
        then:
        codec.read(reader).toString() == "50000000000000000"
    }

    def "Error for short"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("f70af5f6f3c84305000000000000"))
        codec.read(reader)
        then:
        thrown(IndexOutOfBoundsException)
    }
}
