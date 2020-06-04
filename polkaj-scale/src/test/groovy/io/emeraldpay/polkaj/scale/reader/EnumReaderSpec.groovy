package io.emeraldpay.polkaj.scale.reader

import io.emeraldpay.polkaj.scale.ScaleCodecReader
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class EnumReaderSpec extends Specification {

    def "Read value"() {
        setup:
        def codec = new ScaleCodecReader(Hex.decodeHex("0001020302"))
        def rdr = new EnumReader(TestEnum.values())
        when:
        def act = codec.read(rdr)
        then:
        act == TestEnum.ZERO

        when:
        def actAll = [
                codec.read(rdr), codec.read(rdr), codec.read(rdr), codec.read(rdr)
        ]
        then:
        actAll == [TestEnum.ONE, TestEnum.TWO, TestEnum.THREE, TestEnum.TWO]
    }

    def "Fail to read unknown enum id"() {
        setup:
        def codec = new ScaleCodecReader(Hex.decodeHex("04"))
        def rdr = new EnumReader(TestEnum.values())
        when:
        codec.read(rdr)
        then:
        thrown(IllegalStateException)
    }

    def "Cannot create without enums list"() {
        when:
        new EnumReader(null)
        then:
        thrown(NullPointerException)

        when:
        new EnumReader([] as TestEnum[])
        then:
        thrown(IllegalArgumentException)
    }

    enum TestEnum {
        ZERO,
        ONE,
        TWO,
        THREE
    }
}
