package io.emeraldpay.polkaj.schnorrkel

import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class SchnorrkelSpec extends Specification {

    def "Runs"() {
        when:
        def act = Schnorrkel.sign("".bytes,
                new Schnorrkel.Keypair(
                        Hex.decodeHex("46ebddef8cd9bb167dc30878d7113b7e168e6f0646beffd77d69d39bad76b47a"),
                        Hex.decodeHex(
                                "28b0ae221c6bb06856b287f60d7ea0d98552ea5a16db16956849aa371db3eb51" +
                                "fd190cce74df356432b410bd64682309d6dedb27c76845daf388557cbac3ca34"
                        )
                        ))
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
}
