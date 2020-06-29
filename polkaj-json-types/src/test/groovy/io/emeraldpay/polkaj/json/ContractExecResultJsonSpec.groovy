package io.emeraldpay.polkaj.json

import io.emeraldpay.polkaj.types.Address
import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification

class ContractExecResultJsonSpec extends Specification {

    def "Equals and HashCode works for main"() {
        when:
        def v = EqualsVerifier.forClass(ContractExecResultJson)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }

    def "Equals and HashCode works for success"() {
        when:
        def v = EqualsVerifier.forClass(ContractExecResultJson.Success)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }

}
