package io.emeraldpay.polkaj.schnorrkel

import spock.lang.Specification

class SchnorrkelSpec extends Specification {

    def "Runs"() {
        when:
        def act = Schnorrkel.sign("world".bytes, new Schnorrkel.Keypair("pub".bytes, "private".bytes));
        then:
        act == "Hello, ${5 + 7}!"
    }
}
