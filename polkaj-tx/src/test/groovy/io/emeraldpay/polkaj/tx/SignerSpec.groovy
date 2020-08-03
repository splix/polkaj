package io.emeraldpay.polkaj.tx

import io.emeraldpay.polkaj.scaletypes.BalanceTransfer
import io.emeraldpay.polkaj.types.Address
import io.emeraldpay.polkaj.types.DotAmount
import io.emeraldpay.polkaj.types.Hash256
import io.emeraldpay.polkaj.types.Hash512
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class SignerSpec extends Specification {

    def "Accept valid signature"() {
        setup:
        ExtrinsicContext extrinsic = ExtrinsicContext.newBuilder()
            .genesis(Hash256.from("0x35170a58d341fd81c07ee349438da400ecfb625782cd25e29774203080a54f45"))
            .nonce(3)
            .build()
        BalanceTransfer call = new BalanceTransfer(0, 0xff).tap {
            destination = Address.from("5GHZtDpKVWipNuXSAPejAwQremYtRc5ThgSnwwEwZX4tD15W")
            balance = DotAmount.fromDots(5)
        }
        Hash512 signature = Hash512.from("c240525fafbfd8c518df651afae9cc7d429fc90090d5ebba5e2afefecfb57d3dc24f01d57a5ad29814892cc4aeb74474e89df9470557e5ad4c728c86d51ec68f")
        //pub: 86a67224bcecf8f5d05009cdbda4189b678512533aac7a247477c2a18478021e
        Address from = Address.from("5F7FidAQq26VgUYkNvXp3CuraVC36za9bpNaTVZyDs9TX5Q5")
        when:
        def act = Signer.isValid(extrinsic, call, signature, from)
        then:
        Hex.encodeHexString(Signer.getPayload(extrinsic, call)) == "0600ffbac048a77567add318d3b8c3b06b67203fcd9c8137fa2802253a3de95e92f3250b005039278c04000c00fe0000000100000035170a58d341fd81c07ee349438da400ecfb625782cd25e29774203080a54f4535170a58d341fd81c07ee349438da400ecfb625782cd25e29774203080a54f45"
        act == true
    }

    def "Do not accept invalid signature"() {
        setup:
        ExtrinsicContext extrinsic = ExtrinsicContext.newBuilder()
                .genesis(Hash256.from("0x35170a58d341fd81c07ee349438da400ecfb625782cd25e29774203080a54f45"))
                .nonce(3)
                .build()
        BalanceTransfer call = new BalanceTransfer(0, 0xff).tap {
            destination = Address.from("5GHZtDpKVWipNuXSAPejAwQremYtRc5ThgSnwwEwZX4tD15W")
            balance = DotAmount.fromDots(5)
        }
        Hash512 signature = Hash512.from("f240525fafbfd8c518df651afae9cc7d429fc90090d5ebba5e2afefecfb57d3dc24f01d57a5ad29814892cc4aeb74474e89df9470557e5ad4c728c86d51ec68f")
        //pub: 86a67224bcecf8f5d05009cdbda4189b678512533aac7a247477c2a18478021e
        Address from = Address.from("5F7FidAQq26VgUYkNvXp3CuraVC36za9bpNaTVZyDs9TX5Q5")
        when:
        def act = Signer.isValid(extrinsic, call, signature, from)
        then:
        act == false
    }

    def "Build payload"() {
        setup:
        ExtrinsicContext extrinsic = ExtrinsicContext.newBuilder()
                .genesis(Hash256.from("e9540f3b1a920b217847a06682d2ca1a2e416ed809c4ae709da0580edd56ee4b"))
                .nonce(3)
                .build()
        BalanceTransfer call = new BalanceTransfer(0, 0xff).tap {
            // pub: bac048a77567add318d3b8c3b06b67203fcd9c8137fa2802253a3de95e92f325
            destination = Address.from("5GHZtDpKVWipNuXSAPejAwQremYtRc5ThgSnwwEwZX4tD15W")
            balance = DotAmount.fromDots(5)
        }
        when:
        def act = Signer.getPayload(extrinsic, call)
        then:
        Hex.encodeHexString(act) == "0600ffbac048a77567add318d3b8c3b06b67203fcd9c8137fa2802253a3de95e92f3250b005039278c04000c00fe00000001000000e9540f3b1a920b217847a06682d2ca1a2e416ed809c4ae709da0580edd56ee4be9540f3b1a920b217847a06682d2ca1a2e416ed809c4ae709da0580edd56ee4b"
    }

}
