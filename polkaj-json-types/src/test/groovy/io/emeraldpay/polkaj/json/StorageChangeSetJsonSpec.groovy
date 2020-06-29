package io.emeraldpay.polkaj.json

import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification

class StorageChangeSetJsonSpec extends Specification {

    def "Equals and HashCode work"() {
        when:
        def v = EqualsVerifier.forClass(StorageChangeSetJson)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }

    def "Equals and HashCode work for KeyValueOption"() {
        when:
        def v = EqualsVerifier.forClass(StorageChangeSetJson.KeyValueOption)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }
}
