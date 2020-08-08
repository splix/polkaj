package io.emeraldpay.polkaj.scaletypes

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification

class AccountInfoSpec extends Specification {

    def "Equals"() {
        when:
        def v = EqualsVerifier.forClass(AccountInfo)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }
}
