package io.emeraldpay.polkaj.scaletypes

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification

class AccountDataSpec extends Specification {

    def "Equals"() {
        when:
        def v = EqualsVerifier.forClass(AccountData)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }
}
