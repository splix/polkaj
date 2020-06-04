package io.emeraldpay.polkaj.scale.reader

import io.emeraldpay.polkaj.scale.ScaleCodecReader
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class StringReaderSpec extends Specification {

    StringReader reader = ScaleCodecReader.STRING

    def "Read"() {
        when:
        // Hello World!
        // 48 65 6c 6c 6f 20 57 6f 72 6c 64 21
        // 12 << 2 + 0b00 = 0x30
        def codec = new ScaleCodecReader(Hex.decodeHex( "30" + "48656c6c6f20576f726c6421"))
        def act = codec.read(reader)
        then:
        act == "Hello World!"
    }
}
