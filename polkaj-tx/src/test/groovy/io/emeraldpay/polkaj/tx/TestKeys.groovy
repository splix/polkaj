package io.emeraldpay.polkaj.tx

import io.emeraldpay.polkaj.schnorrkel.Schnorrkel
import io.emeraldpay.polkaj.types.Address
import org.apache.commons.codec.binary.Hex

class TestKeys {

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

    public static final Schnorrkel.KeyPair aliceKey = Schnorrkel.getInstance().generateKeyPairFromSeed(
            Hex.decodeHex("e5be9a5092b81bca64be81d212e7f2f9eba183bb7a90954f7b76361f6edb5c0a")
    )
    public static final Address alice = Address.from("5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY")
    public static final Address bob = Address.from("5FHneW46xGXgs5mUiveU4sbTyGBzmstUspZC92UhjJM694ty")
}
