package io.emeraldpay.polkaj.tx

import io.emeraldpay.polkaj.types.Address
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class HashingSpec extends Specification {

    def "hash with xxhash"() {
        expect:
        Hex.encodeHexString(Hashing.xxhash128(str)) == hex
        where:
        hex                                 | str
        "5c0d1176a568c1f92944340dbfed9e9c"  | "Sudo"
        "530ebca703c85910e7164cb7d1c9e47b"  | "Key"
    }

    def "hash with blake2"() {
        expect:
        Hex.encodeHexString(Hashing.blake2(Hex.decodeHex(str))) == hex
        where:
        hex                                                                 | str
        "2e3fb4c297a84c5cebc0e78257d213d0927ccc7596044c6ba013dd05522aacba"  | "d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d"
    }

    def "hash address with blake2"() {
        expect:
        Hex.encodeHexString(Hashing.blake2(Address.from(address))) == hex
        where:
        hex                                                                 | address
        "2e3fb4c297a84c5cebc0e78257d213d0927ccc7596044c6ba013dd05522aacba"  | "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY"
    }
}
