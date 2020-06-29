package io.emeraldpay.polkaj.json

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification

class ReadProofJsonSpec extends Specification {

    def "Equals and HashCode work"() {
        when:
        def v = EqualsVerifier.forClass(ReadProofJson)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }
}
