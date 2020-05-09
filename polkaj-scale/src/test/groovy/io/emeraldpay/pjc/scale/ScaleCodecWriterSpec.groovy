package io.emeraldpay.pjc.scale

import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class ScaleCodecWriterSpec extends Specification {

    ByteArrayOutputStream buf = new ByteArrayOutputStream()
    ScaleCodecWriter codec = new ScaleCodecWriter(buf)

    def "Flush on output stream"() {
        setup:
        OutputStream os = Mock()
        ScaleCodecWriter codec = new ScaleCodecWriter(os)
        when:
        codec.flush()
        then:
        1 * os.flush()
    }

    def "Close on output stream"() {
        setup:
        OutputStream os = Mock()
        ScaleCodecWriter codec = new ScaleCodecWriter(os)
        when:
        codec.close()
        then:
        1 * os.close()
    }

    def "Write unsigned 8-bit integer"() {
        when:
        codec.directWrite(0)
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "00"
    }

    def "Write java-negative integer"() {
        when:
        codec.directWrite(240)
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "f0"
    }

    def "Write unsigned 16-bit integer"() {
        when:
        codec.writeUint16(42)
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "2a00"
    }

    def "Write unsigned 32-bit integer"() {
        when:
        codec.writeUint32(16777215)
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "ffffff00"
    }

    def "Write unsigned 32-bit long"() {
        when:
        codec.writeUint32(16777215L)
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "ffffff00"
    }

    def "Write byte array"() {
        expect:
        codec.writeByteArray(Hex.decodeHex(value))
        Hex.encodeHexString(buf.toByteArray()) == hex
        where:
        hex         | value
        "00"        | ""
        "0401"      | "01"
    }

    def "Write 256 bit value"() {
        when:
        codec.writeUint256(Hex.decodeHex("bb931fd17f85fb26e8209eb7af5747258163df29a7dd8f87fa7617963fcfa1aa"))
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "bb931fd17f85fb26e8209eb7af5747258163df29a7dd8f87fa7617963fcfa1aa"
    }

    def "Error to write short 256 bit value"() {
        when:
        codec.writeUint256(Hex.decodeHex("bb931fd17f85fb26e8209eb7af5747258163df29a7dd8f87fa7617963fcfa1"))
        then:
        thrown(IllegalArgumentException)
    }

    def "Write status message"() {
        when:
        codec.writeUint32(1536) // version
        codec.writeUint32(768) // min version
        codec.directWrite(0) // roles
        codec.directWrite(1) //?
        codec.writeUint32(381) // height
        codec.writeUint256(Hex.decodeHex("bb931fd17f85fb26e8209eb7af5747258163df29a7dd8f87fa7617963fcfa1aa")) // best hash
        codec.writeUint256(Hex.decodeHex("b0a8d493285c2df73290dfb7e61f870f17b41801197a149ca93654499ea3dafe")) // genesis
        codec.writeUint16(0x0004) //

        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "000600000003000000017d010000bb931fd17f85fb26e8209eb7af5747258163df29a7dd8f87fa7617963fcfa1aab0a8d493285c2df73290dfb7e61f870f17b41801197a149ca93654499ea3dafe0400"
    }
}
