package io.emeraldpay.polkaj.tx

import io.emeraldpay.polkaj.scaletypes.BalanceTransfer
import io.emeraldpay.polkaj.scaletypes.BalanceTransferWriter
import io.emeraldpay.polkaj.types.DotAmount
import io.emeraldpay.polkaj.types.Hash256
import io.emeraldpay.polkaj.types.Hash512
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class ExtrinsicSignerSpec extends Specification {

    def "Sign standard"() {
        // PAYLOAD STRUCTURE:
        // a4 <- encoded call size (omitted for signature)
        // 0500 <- method
        // 8eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a48 <- to
        // 0b <- amount size
        // 0b0030ef7dba02 <- amount
        // 00 <- era
        // 1c <- nonce
        // 00 <- tip
        // 12000000 <- runtime version
        // 03000000 <- tx version
        // 4c0bdd177c17ca145ad9a3e76d092d4d4baa8add4fa8c78cc2fbbf8e3cbd5122 <- genesis
        // 4c0bdd177c17ca145ad9a3e76d092d4d4baa8add4fa8c78cc2fbbf8e3cbd5122 <- block

        setup:
        ExtrinsicContext extrinsic = ExtrinsicContext.newBuilder()
                .runtime(3, 0x12)
                .genesis(Hash256.from("0x4c0bdd177c17ca145ad9a3e76d092d4d4baa8add4fa8c78cc2fbbf8e3cbd5122"))
                .nonce(7)
                .build()
        BalanceTransfer call = new BalanceTransfer(5, 0).tap {
            destination = TestKeys.bob
            balance = DotAmount.fromDots(300) // 02 ba 7d ef 30 00 -> 0030ef7dba02
        }
        ExtrinsicSigner signer = new ExtrinsicSigner<>(new BalanceTransferWriter())
        when:
        def payload = signer.getPayload(extrinsic, call)
        then:
        Hex.encodeHexString(payload) == "a405008eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a480b0030ef7dba02001c0012000000030000004c0bdd177c17ca145ad9a3e76d092d4d4baa8add4fa8c78cc2fbbf8e3cbd51224c0bdd177c17ca145ad9a3e76d092d4d4baa8add4fa8c78cc2fbbf8e3cbd5122"

        when:
        def valid = signer.isValid(extrinsic, call,
                Hash512.from("0x02151a3c8c0cea52ecfd5a56ca1dd29dc5ef3984023c8374a1b52a7ed8f3ac4949b7171ae2ee4aae6a28cad94a54cfb9c2154e03bd97a44db546eb69c0f2e98f"),
                TestKeys.alice
        )
        then:
        valid
    }

    def "Sign small amount"() {
        setup:
        ExtrinsicContext extrinsic = ExtrinsicContext.newBuilder()
                .runtime(3, 0x12)
                .genesis(Hash256.from("0x4c0bdd177c17ca145ad9a3e76d092d4d4baa8add4fa8c78cc2fbbf8e3cbd5122"))
                .nonce(7)
                .build()
        BalanceTransfer call = new BalanceTransfer(5, 0).tap {
            destination = TestKeys.bob
            balance = DotAmount.fromPlancks(1)
        }
        ExtrinsicSigner signer = new ExtrinsicSigner<>(new BalanceTransferWriter())
        when:
        def payload = signer.getPayload(extrinsic, call)
        then:
        Hex.encodeHexString(payload) == "8c05008eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a4804001c0012000000030000004c0bdd177c17ca145ad9a3e76d092d4d4baa8add4fa8c78cc2fbbf8e3cbd51224c0bdd177c17ca145ad9a3e76d092d4d4baa8add4fa8c78cc2fbbf8e3cbd5122"

        when:
        def valid = signer.isValid(extrinsic, call,
                Hash512.from("0xa4f970f72a300871f79b9a062dfdc7f0d08c2bfb921f8caa6cbe5abf2b81ff50ca7a0c565031d983d3967bb0b42b67bd013f3ad22e97c9547965ecc81e6a3f80"),
                TestKeys.alice
        )
        then:
        valid
    }

    def "Sign large nonce"() {
        setup:
        ExtrinsicContext extrinsic = ExtrinsicContext.newBuilder()
                .runtime(3, 0x12)
                .genesis(Hash256.from("0x4c0bdd177c17ca145ad9a3e76d092d4d4baa8add4fa8c78cc2fbbf8e3cbd5122"))
                .nonce(1234567890)
                .build()
        BalanceTransfer call = new BalanceTransfer(5, 0).tap {
            destination = TestKeys.bob
            balance = DotAmount.fromDots(123)
        }
        ExtrinsicSigner signer = new ExtrinsicSigner<>(new BalanceTransferWriter())
        when:
        def payload = signer.getPayload(extrinsic, call)
        then:
        Hex.encodeHexString(payload) == "a405008eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a480b008cb6611e010003d20296490012000000030000004c0bdd177c17ca145ad9a3e76d092d4d4baa8add4fa8c78cc2fbbf8e3cbd51224c0bdd177c17ca145ad9a3e76d092d4d4baa8add4fa8c78cc2fbbf8e3cbd5122"

        when:
        def valid = signer.isValid(extrinsic, call,
                Hash512.from("0x6a141ade40871c076f3eb32362f0204db49e4ae37e5dc7a68329f1a6768034556201432b1635637fc1d42ae6fce996fb25ef175ee1ae4015d2b8769436d89987"),
                TestKeys.alice
        )
        then:
        valid
    }

    def "Validate own signature"() {
        setup:
        ExtrinsicSigner signer = new ExtrinsicSigner<>(new BalanceTransferWriter())
        expect:
        ExtrinsicContext context = ExtrinsicContext.newBuilder()
                .runtime(3, 0x12)
                .genesis(Hash256.from("0x4c0bdd177c17ca145ad9a3e76d092d4d4baa8add4fa8c78cc2fbbf8e3cbd5122"))
                .nonce(nonce)
                .build()
        BalanceTransfer call = new BalanceTransfer(5, 0).tap {
            destination = TestKeys.bob
            balance = DotAmount.fromDots(amount)
        }
        def signature = signer.sign(context, call, TestKeys.aliceKey)
        signer.isValid(context, call, signature, TestKeys.alice)
        where:
        amount      | nonce
        1.0         | 0
        100.0       | 100
        1.1234      | 256
    }
}
