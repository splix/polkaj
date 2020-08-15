package io.emeraldpay.polkaj.scale

import io.emeraldpay.polkaj.types.ByteData
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class ScaleExtractSpec extends Specification {

    def "Extracts from bytes array"() {
        List<byte[]> source = [
                "3048656c6c6f20576f726c6421"
        ].collect { Hex.decodeHex(it) }
        when:
        def act = source
                .stream()
                .map(ScaleExtract.fromBytesArray(ScaleCodecReader.STRING))
                .find()
        then:
        act == "Hello World!"
    }

    def "Extracts from bytes data"() {
        List<ByteData> source = [
                "3048656c6c6f20576f726c6421"
        ].collect { ByteData.from(it) }
        when:
        def act = source
                .stream()
                .map(ScaleExtract.fromBytesData(ScaleCodecReader.STRING))
                .find()
        then:
        act == "Hello World!"
    }

    def "Cannot create without reader"() {
        when:
        ScaleExtract.fromBytesArray(null)
        then:
        thrown(NullPointerException)

        when:
        ScaleExtract.fromBytesData(null)
        then:
        thrown(NullPointerException)
    }
}
