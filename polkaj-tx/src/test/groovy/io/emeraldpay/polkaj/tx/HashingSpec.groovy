package io.emeraldpay.polkaj.tx

import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class HashingSpec extends Specification {

    def "hash xxhash"() {
        expect:
        Hex.encodeHexString(Hashing.xxhash128(str)) == hex
        where:
        hex                                 | str
        "5c0d1176a568c1f92944340dbfed9e9c"  | "Sudo"
        "530ebca703c85910e7164cb7d1c9e47b"  | "Key"
    }

}
