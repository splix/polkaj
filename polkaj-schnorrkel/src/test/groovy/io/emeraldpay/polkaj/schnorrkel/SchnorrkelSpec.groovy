package io.emeraldpay.polkaj.schnorrkel

import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

import java.security.SecureRandom

class SchnorrkelSpec extends Specification {

    def key1 = new Schnorrkel.KeyPair(
        Hex.decodeHex("46ebddef8cd9bb167dc30878d7113b7e168e6f0646beffd77d69d39bad76b47a"),
        Hex.decodeHex(
            "28b0ae221c6bb06856b287f60d7ea0d98552ea5a16db16956849aa371db3eb51" +
            "fd190cce74df356432b410bd64682309d6dedb27c76845daf388557cbac3ca34"
        )
    )

    def "Can sign"() {
        when:
        def act = Schnorrkel.sign("".bytes, key1)
        then:
        act != null
        act.length == 64
    }

    def "Throws error on short sk"() {
        when:
        Schnorrkel.sign("".bytes,
                new Schnorrkel.KeyPair(
                        Hex.decodeHex("46ebddef8cd9bb167dc30878d7113b7e168e6f0646beffd77d69d39bad76b47a"),
                        Hex.decodeHex(
                                "28b0"
                        )
                ));
        then:
        def t = thrown(SchnorrkelException)
        t.message.length() > 0
        t.message == "SecretKey must be 64 bytes in length"
    }

    def "Signature is valid"() {
        setup:
        byte[] msg = "hello".bytes
        when:
        byte[] signature = Schnorrkel.sign(msg, key1)
        def act = Schnorrkel.verify(signature, msg, key1)
        then:
        act == true
    }

    def "Modified signature is invalid"() {
        setup:
        byte[] msg = "hello".bytes
        when:
        byte[] signature = Schnorrkel.sign(msg, key1)
        def initial = Schnorrkel.verify(signature, msg, key1)
        then:
        initial == true

        when:
        signature[0] = (byte)(signature[0] + 1)
        def act = Schnorrkel.verify(signature, msg, key1)
        then:
        act == false
    }

    def "Different signature is invalid"() {
        setup:
        byte[] msg = "hello".bytes
        when:
        byte[] signature = Schnorrkel.sign(msg, key1)
        byte[] signature2 = Schnorrkel.sign("hello2".bytes, key1)
        def act = Schnorrkel.verify(signature2, msg, key1)
        then:
        act == false
    }

    def "Throws error on invalid signature"() {
        setup:
        byte[] msg = "hello".bytes
        when:
        Schnorrkel.verify(Hex.decodeHex("00112233"), msg, key1)
        then:
        thrown(SchnorrkelException)
    }

    def "Throws error on invalid pubkey"() {
        setup:
        byte[] msg = "hello".bytes
        when:
        byte[] signature = Schnorrkel.sign(msg, key1)
        Schnorrkel.verify(signature, msg, new Schnorrkel.PublicKey(Hex.decodeHex("11223344")))
        then:
        thrown(SchnorrkelException)
    }

    def "Generates working key"() {
        setup:
        def random = SecureRandom.instanceStrong
        byte[] msg = "hello".bytes
        when:
        def keypair = Schnorrkel.generateKeyPair(random)
        then:
        keypair != null
        keypair.publicKey.length == Schnorrkel.PUBLIC_KEY_LENGTH
        keypair.secretKey.length == Schnorrkel.SECRET_KEY_LENGTH
        new BigInteger(1, keypair.publicKey) != BigInteger.ZERO
        new BigInteger(1, keypair.secretKey) != BigInteger.ZERO

        when:
        byte[] signature = Schnorrkel.sign(msg, keypair)
        def act = Schnorrkel.verify(signature, msg, keypair)
        then:
        act
    }

    def "Generates key from default Secure Random"() {
        when:
        def keypair = Schnorrkel.generateKeyPair()
        then:
        keypair != null
        keypair.publicKey.length == Schnorrkel.PUBLIC_KEY_LENGTH
        keypair.secretKey.length == Schnorrkel.SECRET_KEY_LENGTH
        new BigInteger(1, keypair.publicKey) != BigInteger.ZERO
        new BigInteger(1, keypair.secretKey) != BigInteger.ZERO
    }

    def "Generates from seed"() {
        when:
        def keypair = Schnorrkel.generateKeyPairFromSeed(Hex.decodeHex("fac7959dbfe72f052e5a0c3c8d6530f202b02fd8f9f5ca3580ec8deb7797479e"))
        then:
        keypair != null
        keypair.publicKey.length == Schnorrkel.PUBLIC_KEY_LENGTH
        keypair.secretKey.length == Schnorrkel.SECRET_KEY_LENGTH
        new BigInteger(1, keypair.publicKey) != BigInteger.ZERO
        new BigInteger(1, keypair.secretKey) != BigInteger.ZERO
        Hex.encodeHexString(keypair.getPublicKey()) == "46ebddef8cd9bb167dc30878d7113b7e168e6f0646beffd77d69d39bad76b47a"
    }

    def "Derive key"() {
        setup:
        def seed = Hex.decodeHex("fac7959dbfe72f052e5a0c3c8d6530f202b02fd8f9f5ca3580ec8deb7797479e")
        def cc = Hex.decodeHex("14416c6963650000000000000000000000000000000000000000000000000000") // Alice
        when:
        def base = Schnorrkel.generateKeyPairFromSeed(seed)
        def keypair = Schnorrkel.deriveKeyPair(base, cc)
        then:
        Hex.encodeHexString(keypair.getPublicKey()) == "d43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d"
    }
}
