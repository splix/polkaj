package io.emeraldpay.polkaj.scaletypes

import io.emeraldpay.polkaj.scale.ScaleCodecReader
import io.emeraldpay.polkaj.types.DotAmount
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class AccountInfoReaderSpec extends Specification {

    AccountInfoReader reader = new AccountInfoReader()

    def "Read value"() {
        setup:
        def value = Hex.decodeHex("110000000300000004000000f70af5f6f3c843050000000000000000000000000000000000000000000000000000c52ebca2b10000000000000000000000c52ebca2b1000000000000000000")
        when:
        def act = new ScaleCodecReader(value).read(reader)
        then:
        act != null
        act.nonce == 17
        act.consumers == 3
        act.providers == 4
        with(act.data) {
            free == DotAmount.fromPlancks(379367743775116023)
            reserved == DotAmount.ZERO
            feeFrozen == DotAmount.fromDots(5000000)
            miscFrozen == DotAmount.fromDots(5000000)
        }
    }
}
