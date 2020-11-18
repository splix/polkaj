package io.emeraldpay.polkaj.scaletypes

import io.emeraldpay.polkaj.scale.ScaleCodecReader
import io.emeraldpay.polkaj.ss58.SS58Type
import io.emeraldpay.polkaj.types.Address
import io.emeraldpay.polkaj.types.DotAmount
import io.emeraldpay.polkaj.types.Hash512
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class ExtrinsicReaderSpec extends Specification {

    def "Parse transfer"() {
        setup:
        def existing = "390284b8fdf4f080eeaa6d3f32a445c91c7effa6ffef16d5fe81783837ab7a23602b3b01bc11655de6e7461b0951353db25f4aaf67a58db547fa3a2f20cbcd7772ba715f8ccbe9d8bddf253c7f6e6f6acb83848a7da1f27de248afca10d3291de92ede8ce5000c000400483eae8765348ef3e347e6b55995f99353223a8b28cf63829554933bcd5e801d0780cff40808"
        ExtrinsicReader<BalanceTransfer> reader = new ExtrinsicReader<>(
                new BalanceTransferReader(SS58Type.Network.CANARY),
                SS58Type.Network.CANARY
        )
        when:
        def rdr = new ScaleCodecReader(Hex.decodeHex(existing))
        def act = reader.read(rdr)

        then:
        with(act.tx) {
            sender == Address.from("GksmaqmLPbfQhsNgT2S5GcwwTkGXCpkPU8FDzxP4siKPAVu")
            era == 229
            nonce == 3
            tip == DotAmount.ZERO
            signature.getType() == Extrinsic.SignatureType.SR25519
            signature.getValue() == Hash512.from("0xbc11655de6e7461b0951353db25f4aaf67a58db547fa3a2f20cbcd7772ba715f8ccbe9d8bddf253c7f6e6f6acb83848a7da1f27de248afca10d3291de92ede8c")
        }
        with(act.call) {
            moduleIndex == 4
            callIndex == 0
            destination == Address.from("ED3aw4s68wTDscCbWnCCw94qSrkA1D8HcUXC8ytaoM2X2xd")
            balance == DotAmount.fromDots(3.451)
        }
    }
}
