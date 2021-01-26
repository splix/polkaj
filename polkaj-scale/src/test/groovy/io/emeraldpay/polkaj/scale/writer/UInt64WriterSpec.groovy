package io.emeraldpay.polkaj.scale.writer

import io.emeraldpay.polkaj.scale.ScaleCodecWriter
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class UInt64WriterSpec extends Specification {

    UInt64Writer writer = new UInt64Writer()
    ByteArrayOutputStream buf = new ByteArrayOutputStream()
    ScaleCodecWriter codec = new ScaleCodecWriter(buf)

    def "Writes"() {
        when:
        codec.write(writer, new BigInteger("379367743775116023"))
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "f70af5f6f3c84305"
    }

    def "Writes with zero prefix"() {
        when:
        codec.write(writer, new BigInteger("50000000000000000"))
        def act = buf.toByteArray()
        then:
        Hex.encodeHexString(act) == "0000c52ebca2b100"
    }

    def "Error for negative number"() {
        when:
        codec.write(writer, BigInteger.valueOf(-1))
        then:
        thrown(IllegalArgumentException)
    }

    def "Error for large number"() {
        when:
        codec.write(writer, UInt64Writer.MAX_UINT64 + 1)
        then:
        thrown(IllegalArgumentException)
    }
}
