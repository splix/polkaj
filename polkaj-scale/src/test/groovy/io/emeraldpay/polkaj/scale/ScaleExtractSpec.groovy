package io.emeraldpay.polkaj.scale

import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class ScaleExtractSpec extends Specification {

    def "Extracts from bytes"() {
        List<byte[]> source = [
                "3048656c6c6f20576f726c6421"
        ].collect { Hex.decodeHex(it) }
        when:
        def act = source
                .stream()
                .map(ScaleExtract.fromBytes(ScaleCodecReader.STRING))
                .find()
        then:
        act == "Hello World!"
    }

    def "Cannot create without reader"() {
        when:
        ScaleExtract.fromBytes(null)
        then:
        thrown(NullPointerException)
    }
}
