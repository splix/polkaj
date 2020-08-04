package io.emeraldpay.polkaj.tx

import io.emeraldpay.polkaj.scaletypes.BalanceTransfer
import io.emeraldpay.polkaj.schnorrkel.Schnorrkel
import io.emeraldpay.polkaj.types.Address
import io.emeraldpay.polkaj.types.DotAmount
import io.emeraldpay.polkaj.types.Hash256
import io.emeraldpay.polkaj.types.Hash512
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class SignerSpec extends Specification {

    // Secret Key URI `//Alice` is account:
    //  Network ID/version: substrate
    //  Secret seed:        0xe5be9a5092b81bca64be81d212e7f2f9eba183bb7a90954f7b76361f6edb5c0a
    //  Public key (hex):   0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d
    //  Account ID:         0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d
    //  SS58 Address:       5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY

    // Secret Key URI `//Bob` is account:
    //  Network ID/version: substrate
    //  Secret seed:        0x398f0c28f98885e046333d4a41c19cee4c37368a9832c6502f6cfd182e2aef89
    //  Public key (hex):   0x8eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a48
    //  Account ID:         0x8eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a48
    //  SS58 Address:       5FHneW46xGXgs5mUiveU4sbTyGBzmstUspZC92UhjJM694ty

    Schnorrkel.KeyPair aliceKey = Schnorrkel.generateKeyPairFromSeed(
            Hex.decodeHex("e5be9a5092b81bca64be81d212e7f2f9eba183bb7a90954f7b76361f6edb5c0a")
    )
    Address alice = Address.from("5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY")
    Address bob = Address.from("5FHneW46xGXgs5mUiveU4sbTyGBzmstUspZC92UhjJM694ty")

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
            destination = bob
            balance = DotAmount.fromDots(3) // 02 ba 7d ef 30 00 -> 0030ef7dba02
        }
        when:
        def payload = Signer.getPayload(extrinsic, call)
        then:
        Hex.encodeHexString(payload) == "a405008eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a480b0030ef7dba02001c0012000000030000004c0bdd177c17ca145ad9a3e76d092d4d4baa8add4fa8c78cc2fbbf8e3cbd51224c0bdd177c17ca145ad9a3e76d092d4d4baa8add4fa8c78cc2fbbf8e3cbd5122"

        when:
        def valid = Signer.isValid(extrinsic, call,
                Hash512.from("0x02151a3c8c0cea52ecfd5a56ca1dd29dc5ef3984023c8374a1b52a7ed8f3ac4949b7171ae2ee4aae6a28cad94a54cfb9c2154e03bd97a44db546eb69c0f2e98f"),
                alice
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
            destination = bob
            balance = DotAmount.fromPlancks(1)
        }
        when:
        def payload = Signer.getPayload(extrinsic, call)
        then:
        Hex.encodeHexString(payload) == "8c05008eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a4804001c0012000000030000004c0bdd177c17ca145ad9a3e76d092d4d4baa8add4fa8c78cc2fbbf8e3cbd51224c0bdd177c17ca145ad9a3e76d092d4d4baa8add4fa8c78cc2fbbf8e3cbd5122"

        when:
        def valid = Signer.isValid(extrinsic, call,
                Hash512.from("0xa4f970f72a300871f79b9a062dfdc7f0d08c2bfb921f8caa6cbe5abf2b81ff50ca7a0c565031d983d3967bb0b42b67bd013f3ad22e97c9547965ecc81e6a3f80"),
                alice
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
            destination = bob
            balance = DotAmount.fromDots(1.23)
        }
        when:
        def payload = Signer.getPayload(extrinsic, call)
        then:
        Hex.encodeHexString(payload) == "a405008eaf04151687736326c9fea17e25fc5287613693c912909cb226aa4794f26a480b008cb6611e010003d20296490012000000030000004c0bdd177c17ca145ad9a3e76d092d4d4baa8add4fa8c78cc2fbbf8e3cbd51224c0bdd177c17ca145ad9a3e76d092d4d4baa8add4fa8c78cc2fbbf8e3cbd5122"

        when:
        def valid = Signer.isValid(extrinsic, call,
                Hash512.from("0x6a141ade40871c076f3eb32362f0204db49e4ae37e5dc7a68329f1a6768034556201432b1635637fc1d42ae6fce996fb25ef175ee1ae4015d2b8769436d89987"),
                alice
        )
        then:
        valid
    }

}
