package io.emeraldpay.polkaj.tx

import io.emeraldpay.polkaj.scaletypes.BalanceReader
import io.emeraldpay.polkaj.types.Address
import io.emeraldpay.polkaj.types.ByteData
import io.emeraldpay.polkaj.types.DotAmount
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class BalanceRequestSpec extends Specification {

    def "Prepare issuance request"() {
        when:
        def req = BalanceRequest.totalIssuance()
        def act = Hex.encodeHexString(req.requestData().bytes)
        then:
        act == "c2261276cc9d1f8598ea4b6a74b15c2f57c875e4cff74148e4628f264b974c80"
    }

    def "Decode issuance response"() {
        when:
        def req = BalanceRequest.totalIssuance()
        def act = req.apply(ByteData.from("0xf70af5f6f3c843050000000000000000"))
        then:
        act == DotAmount.fromPlancks("379367743775116023")
    }

    def "Prepare balance request"() {
        when:
        def req = BalanceRequest.balanceOf(Address.from("1WG3jyNqniQMRZGQUc7QD2kVLT8hkRPGMSqAb5XYQM1UDxN"))
        def act = Hex.encodeHexString(req.requestData().bytes)
        then:
        act == "26aa394eea5630e07c48ae0c9558cef7b99d880ec681799c0cf30e8886371da9762b7694480fb50358c23ab18950158b1650c532ed1a8641e8922aa24ade0ff411d03edd9ed1c6b7fe42f1a801cee37c"
    }

    def "Decode balance reponse"() {
        setup:
        def req = BalanceRequest.balanceOf(Address.from("1WG3jyNqniQMRZGQUc7QD2kVLT8hkRPGMSqAb5XYQM1UDxN"))
        def result = ByteData.from("0x1100000003f70af5f6f3c843050000000000000000000000000000000000000000000000000000c52ebca2b10000000000000000000000c52ebca2b1000000000000000000")
        when:
        def act = req.apply(result)
        then:
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
