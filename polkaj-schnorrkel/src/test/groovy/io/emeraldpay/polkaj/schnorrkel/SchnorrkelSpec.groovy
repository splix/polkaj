package io.emeraldpay.polkaj.schnorrkel

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification

class SchnorrkelSpec extends Specification {

    def "Provides native implementation"() {
        when:
        def act = Schnorrkel.getInstance()
        then:
        act != null
        act instanceof SchnorrkelNative
    }

    def "Equals for Pubkey"() {
        when:
        def v = EqualsVerifier.forClass(Schnorrkel.PublicKey)
                .suppress(Warning.STRICT_INHERITANCE)
        then:
        v.verify()
    }

    def "Equals for KeyPair"() {
        when:
        def v = EqualsVerifier.forClass(Schnorrkel.KeyPair)
                .withRedefinedSuperclass()
                .suppress(Warning.STRICT_INHERITANCE)
        then:
        v.verify()
    }

    def "Equals for ChainCode"() {
        when:
        def v = EqualsVerifier.forClass(Schnorrkel.ChainCode)
                .suppress(Warning.STRICT_INHERITANCE)
        then:
        v.verify()
    }
}
