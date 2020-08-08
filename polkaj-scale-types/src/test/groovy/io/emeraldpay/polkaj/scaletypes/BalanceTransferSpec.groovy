package io.emeraldpay.polkaj.scaletypes

import io.emeraldpay.polkaj.ss58.SS58Type
import io.emeraldpay.polkaj.types.Address
import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification

class BalanceTransferSpec extends Specification{

    def "Equals"() {
        when:
        def v = EqualsVerifier.forClass(BalanceTransfer)
                .withPrefabValues(SS58Type.Network,
                        SS58Type.Network.CANARY,
                        SS58Type.Network.LIVE)
                .withPrefabValues(Address,
                        Address.from("GksmaqmLPbfQhsNgT2S5GcwwTkGXCpkPU8FDzxP4siKPAVu"),
                        Address.from("ED3aw4s68wTDscCbWnCCw94qSrkA1D8HcUXC8ytaoM2X2xd"))
                // https://jqno.nl/equalsverifier/errormessages/symmetry-does-not-equal-superclass-instance/
                .withRedefinedSuperclass()
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }
}
