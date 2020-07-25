package io.emeraldpay.polkaj.tx

import io.emeraldpay.polkaj.types.Address
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

import java.nio.ByteBuffer

class HashingSpec extends Specification {

    def "hash with xxhash"() {
        expect:
        ByteBuffer buf = ByteBuffer.allocate(16)
        Hashing.xxhash128(buf, str)
        Hex.encodeHexString(buf.flip().array()) == hex
        where:
        hex                                 | str
        "5c0d1176a568c1f92944340dbfed9e9c"  | "Sudo"
        "530ebca703c85910e7164cb7d1c9e47b"  | "Key"
    }

    def "hash with blake2"() {
        expect:
        ByteBuffer buf = ByteBuffer.allocate(32)
        Hashing.blake2(buf, Hex.decodeHex(str))
        Hex.encodeHexString(buf.flip().array()) == hex
        where:
        hex                                                                 | str
        "2e3fb4c297a84c5cebc0e78257d213d0927ccc7596044c6ba013dd05522aacba"  | "d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d"
    }

    def "hash address with blake2"() {
        expect:
        ByteBuffer buf = ByteBuffer.allocate(32)
        Hashing.blake2(buf, Address.from(address))
        Hex.encodeHexString(buf.flip().array()) == hex
        where:
        hex                                                                 | address
        "2e3fb4c297a84c5cebc0e78257d213d0927ccc7596044c6ba013dd05522aacba"  | "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY"
    }

    def "hash address with blake2 128"() {
        expect:
        ByteBuffer buf = ByteBuffer.allocate(16)
        Hashing.blake2128(buf, Address.from(address))
        Hex.encodeHexString(buf.flip().array()) == hex
        where:
        hex                                 | address
        "762b7694480fb50358c23ab18950158b"  | "1WG3jyNqniQMRZGQUc7QD2kVLT8hkRPGMSqAb5XYQM1UDxN"
    }

    def "combine multiple hashes"() {
        when:
        def buf = ByteBuffer.allocate(16 + 32 + 16)
        Hashing.xxhash128(buf, "Sudo")
        Hashing.blake2(buf, Address.from("5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY"))
        Hashing.blake2128(buf, Address.from("1WG3jyNqniQMRZGQUc7QD2kVLT8hkRPGMSqAb5XYQM1UDxN"))
        def act = Hex.encodeHexString(buf.flip().array())
        then:
        act == "5c0d1176a568c1f92944340dbfed9e9c" + "2e3fb4c297a84c5cebc0e78257d213d0927ccc7596044c6ba013dd05522aacba" + "762b7694480fb50358c23ab18950158b"
    }
}
