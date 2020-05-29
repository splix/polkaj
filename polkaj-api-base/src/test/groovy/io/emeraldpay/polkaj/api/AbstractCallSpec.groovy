package io.emeraldpay.polkaj.api

import com.fasterxml.jackson.databind.ObjectMapper
import io.emeraldpay.polkaj.json.jackson.PolkadotModule
import io.emeraldpay.polkaj.types.Hash256
import spock.lang.Specification

class AbstractCallSpec extends Specification {

    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new PolkadotModule())

    def "Convert to list result"() {
        when:
        def act = new TestCall("test_foo", "")
        act.resultClazz = Hash256.class
        act.expectList()
        then:
        act != null
        act.getResultType(objectMapper.typeFactory).toCanonical() == "java.util.List<io.emeraldpay.polkaj.types.Hash256>"
    }

    def "Cannot convert to list is only java type is set"() {
        setup:
        def act = new TestCall("test_foo", "")
        act.resultType = objectMapper.typeFactory.constructType(Hash256.class)
        when:
        act.expectList()
        then:
        thrown(IllegalStateException)
    }

    def "Cannot convert to list twice"() {
        setup:
        def act = new TestCall("test_foo", "")
        act.resultClazz = Hash256.class
        when:
        act.expectList()
        then:
        act != null
        when:
        act.expectList()
        then:
        thrown(IllegalStateException)
    }

    class TestCall extends AbstractCall {

        protected TestCall(String method, Object[] params) {
            super(method, params)
        }

    }
}
