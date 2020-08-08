package io.emeraldpay.polkaj.scale.writer

import io.emeraldpay.polkaj.scale.ScaleCodecWriter
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class UInt128WriterSpec extends Specification {

    UInt128Writer writer = new UInt128Writer()
    ByteArrayOutputStream buf = new ByteArrayOutputStream()
    ScaleCodecWriter codec = new ScaleCodecWriter(buf)

    def "Writes"() {
        when:
        codec.write(writer, new BigInteger("379367743775116023"))
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "f70af5f6f3c843050000000000000000"
    }

    def "Writes with zero prefix"() {
        when:
        codec.write(writer, new BigInteger("50000000000000000"))
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "0000c52ebca2b1000000000000000000"
    }

    def "Error for negative number"() {
        when:
        codec.write(writer, BigInteger.valueOf(-1))
        then:
        thrown(IllegalArgumentException)
    }

    def "Error for large number"() {
        when:
        codec.write(writer, new BigInteger("ed65cc68bea2f4a89b9f20106d184ca9e72a032d9a82ba5da2eaff0a783f8b1b", 16))
        then:
        thrown(IllegalArgumentException)
    }
}
