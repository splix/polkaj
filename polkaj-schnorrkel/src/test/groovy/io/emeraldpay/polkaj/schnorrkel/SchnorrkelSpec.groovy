package io.emeraldpay.polkaj.schnorrkel

import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class SchnorrkelSpec extends Specification {

    def key1 = new Schnorrkel.Keypair(
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
                new Schnorrkel.Keypair(
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
        def act = Schnorrkel.verify(signature, msg, key1.publicKey)
        then:
        act == true
    }

    def "Modified signature is invalid"() {
        setup:
        byte[] msg = "hello".bytes
        when:
        byte[] signature = Schnorrkel.sign(msg, key1)
        def initial = Schnorrkel.verify(signature, msg, key1.publicKey)
        then:
        initial == true

        when:
        signature[0] = (byte)(signature[0] + 1)
        def act = Schnorrkel.verify(signature, msg, key1.publicKey)
        then:
        act == false
    }

    def "Different signature is invalid"() {
        setup:
        byte[] msg = "hello".bytes
        when:
        byte[] signature = Schnorrkel.sign(msg, key1)
        byte[] signature2 = Schnorrkel.sign("hello2".bytes, key1)
        def act = Schnorrkel.verify(signature2, msg, key1.publicKey)
        then:
        act == false
    }

    def "Throws error on invalid signature"() {
        setup:
        byte[] msg = "hello".bytes
        when:
        Schnorrkel.verify(Hex.decodeHex("00112233"), msg, key1.publicKey)
        then:
        thrown(SchnorrkelException)
    }

    def "Throws error on invalid pubkey"() {
        setup:
        byte[] msg = "hello".bytes
        when:
        byte[] signature = Schnorrkel.sign(msg, key1)
        Schnorrkel.verify(signature, msg, Hex.decodeHex("11223344"))
        then:
        thrown(SchnorrkelException)
    }
}
