package io.emeraldpay.polkaj.scale.reader

import io.emeraldpay.polkaj.scale.ScaleCodecReader
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class ListReaderSpec extends Specification {

    def reader = new ListReader(ScaleCodecReader.UINT16)

    def "Reads list of 16-bit ints"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("18040008000f00100017002a00"))
        then:
        codec.hasNext()
        codec.read(reader) == [4, 8, 15, 16, 23, 42]
        !codec.hasNext()
    }
}
