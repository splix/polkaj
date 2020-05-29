package io.emeraldpay.polkaj.scale

import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class ScaleCodecReaderSpec extends Specification {

    def "Reads unsigned 8-bit integer"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("45"))
        then:
        codec.hasNext()
        codec.readByte() == 69 as byte
        !codec.hasNext()
    }

    def "Reads unsigned 16-bit integer"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("2a00"))
        then:
        codec.hasNext()
        codec.readUint16() == 42
        !codec.hasNext()
    }

    def "Seek and read"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("2a00"))
        then:
        codec.readUint16() == 42
        !codec.hasNext()
        codec.seek(0)
        codec.hasNext()
        codec.readUint16() == 42
        !codec.hasNext()
    }

    def "Cannot seek bellow"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("2a00"))
        codec.seek(-1)
        then:
        thrown(IllegalArgumentException)
    }

    def "Cannot seek over"() {
        setup:
        def codec = new ScaleCodecReader(Hex.decodeHex("2a00"))

        when:
        codec.seek(5)
        then:
        thrown(IllegalArgumentException)

        when:
        codec.seek(2)
        then:
        thrown(IllegalArgumentException)
    }

    def "Can skip backwards"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("2a00"))
        codec.readUint16() == 42
        codec.skip(-2)
        then:
        codec.readUint16() == 42
    }

    def "Cannot skip bellow"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("2a00"))
        codec.readUint16()
        codec.skip(-3)
        then:
        thrown(IllegalArgumentException)
    }

    def "Error to read with null reader"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("2a00"))
        codec.read(null)
        then:
        thrown(NullPointerException)
    }

    def "Reads java-negative byte"() {
        when:
        def codec = new ScaleCodecReader(Hex.decodeHex("f0"))
        then:
        codec.hasNext()
        codec.readUByte() == 240
        !codec.hasNext()
    }

    def "Read status message"() {
        setup:
        def msg = Hex.decodeHex("000600000003000000017d010000bb931fd17f85fb26e8209eb7af5747258163df29a7dd8f87fa7617963fcfa1aab0a8d493285c2df73290dfb7e61f870f17b41801197a149ca93654499ea3dafe0400")
        def rdr = new ScaleCodecReader(msg)

        when:
        def act = rdr.readUint32() // version
        then:
        act == 1536

        when:
        act = rdr.readUint32() // min version
        then:
        act == 768

        when:
        act = rdr.readByte() // roles
        then:
        act == 0.byteValue()

        when:
        rdr.skip(1) // ?
        act = rdr.readUint32() // height
        then:
        act == 381

        when:
        act = rdr.readUint256() // best hash
        then:
        Hex.encodeHexString(act) == "bb931fd17f85fb26e8209eb7af5747258163df29a7dd8f87fa7617963fcfa1aa"

        when:
        act = rdr.readUint256() // genesis hash
        then:
        Hex.encodeHexString(act) == "b0a8d493285c2df73290dfb7e61f870f17b41801197a149ca93654499ea3dafe"
    }

    def "Read byte array"() {
        expect:
        Hex.encodeHexString(new ScaleCodecReader(Hex.decodeHex(hex)).readByteArray()) == value
        where:
        hex         | value
        "00"        | ""
        "0401"      | "01"
    }

    def "Read fixed byte array"() {
        expect:
        Hex.encodeHexString(new ScaleCodecReader(Hex.decodeHex(hex)).readByteArray(32)) == hex
        where:
        hex << ["bb931fd17f85fb26e8209eb7af5747258163df29a7dd8f87fa7617963fcfa1aa"]
    }
}
