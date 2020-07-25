package io.emeraldpay.polkaj.scaletypes

import io.emeraldpay.polkaj.scale.ScaleCodecReader
import io.emeraldpay.polkaj.types.DotAmount
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class AccountInfoReaderSpec extends Specification {

    AccountInfoReader reader = new AccountInfoReader()

    def "Read value"() {
        setup:
        def value = Hex.decodeHex("1100000003f70af5f6f3c843050000000000000000000000000000000000000000000000000000c52ebca2b10000000000000000000000c52ebca2b1000000000000000000")
        when:
        def act = new ScaleCodecReader(value).read(reader)
        then:
        act != null
        act.nonce == 17
        act.refcount == 3
        with(act.data) {
            free == DotAmount.fromPlancks(379367743775116023)
            reserved == DotAmount.ZERO
            feeFrozen == DotAmount.fromDots(50000)
            miscFrozen == DotAmount.fromDots(50000)
        }
    }
}
