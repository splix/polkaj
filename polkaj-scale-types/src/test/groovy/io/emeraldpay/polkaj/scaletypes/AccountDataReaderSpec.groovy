package io.emeraldpay.polkaj.scaletypes

import io.emeraldpay.polkaj.scale.ScaleCodecReader
import io.emeraldpay.polkaj.types.DotAmount
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class AccountDataReaderSpec extends Specification {

    AccountDataReader reader = new AccountDataReader()

    def "Read value"() {
        setup:
        def value = Hex.decodeHex("f70af5f6f3c843050000000000000000000000000000000000000000000000000000c52ebca2b10000000000000000000000c52ebca2b1000000000000000000")
        when:
        def act = new ScaleCodecReader(value).read(reader)
        then:
        act != null
        act.free == DotAmount.fromPlancks(379367743775116023)
        act.reserved == DotAmount.ZERO
        act.feeFrozen == DotAmount.fromDots(50000)
        act.miscFrozen == DotAmount.fromDots(50000)
    }
}
