package io.emeraldpay.polkaj.scale.reader

import io.emeraldpay.polkaj.scale.ScaleCodecReader
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

import java.nio.ByteBuffer

class Int32ReaderSpec extends Specification {

    Int32Reader reader = new Int32Reader()

    def "Read positive"() {
        expect:
        def codec = new ScaleCodecReader(Hex.decodeHex(hex))
        codec.read(reader) == value
        where:
        hex         | value
        "00000000"  | 0
        "01000000"  | 0x01
        "01020304"  | 0x04030201
        "ff000000"  | 0xff
        "ffff0000"  | 0xffff
        "ffffff00"  | 0xffffff
        "ffffff7f"  | Integer.MAX_VALUE
    }

    def "Read negative"() {
        expect:
        def codec = new ScaleCodecReader(Hex.decodeHex(hex))
        codec.read(reader) == value
        where:
        hex         | value
        "ffffffff"  | -1
        "9cffffff"  | -100
        "0100ffff"  | -0xffff
        "fefeffff"  | -0x0102
        "fdfdfeff"  | -0x010203
        "00000080"  | Integer.MIN_VALUE
    }

}
