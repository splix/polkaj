package io.emeraldpay.polkaj.scaletypes

import io.emeraldpay.polkaj.scale.ScaleCodecReader
import io.emeraldpay.polkaj.types.DotAmount
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class BalanceReaderSpec extends Specification {

    BalanceReader reader = new BalanceReader()

    def "Read values"() {
        expect:
        new ScaleCodecReader(Hex.decodeHex(hex)).read(reader) == balance
        where:
        hex                                 | balance
        "f70af5f6f3c843050000000000000000"  | DotAmount.fromPlancks("379367743775116023")
        "0000c52ebca2b1000000000000000000"  | DotAmount.fromDots(5000000)
        "00000000000000000000000000000000"  | DotAmount.ZERO
    }
}
