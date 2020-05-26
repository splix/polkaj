package io.emeraldpay.pjc.api

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import io.emeraldpay.pjc.json.BlockJson
import io.emeraldpay.pjc.json.jackson.PolkadotModule
import io.emeraldpay.pjc.types.Hash256
import spock.lang.Specification

class SubscribeCallSpec extends Specification {

    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new PolkadotModule())

    def "Create with class"() {
        when:
        def act = SubscribeCall.create(String.class, "test_foo", "test_unfoo")
        then:
        act.getMethod() == "test_foo"
        act.getUnsubscribe() == "test_unfoo"
        act.getResultType(objectMapper.typeFactory).toCanonical() == "java.lang.String"
        act.getParams() != null
        act.getParams().size() == 0

        when:
        def act2 = SubscribeCall.create(String.class, "test_foo", "test_unfoo", [])
        then:
        act == act2
    }

    def "Create with class and params"() {
        when:
        def act = SubscribeCall.create(String.class, "test_foo", "test_unfoo", 1, "foo")
        then:
        act.getMethod() == "test_foo"
        act.getUnsubscribe() == "test_unfoo"
        act.getResultType(objectMapper.typeFactory).toCanonical() == "java.lang.String"
        act.getParams() == [1, "foo"].toArray()

        when:
        def act2 = SubscribeCall.create(String.class, "test_foo", "test_unfoo", [1, "foo"])
        
        then:
        act == act2
    }

    def "Create with custom class and params"() {
        when:
        def act = SubscribeCall.create(BlockJson.Header.class, "test_foo", "test_unfoo", 
                Hash256.from("0x5d83f66b61701da4cbd7a60137db89c69469a4f798b62aba9176ab253b423828"))
        then:
        act.getMethod() == "test_foo"
        act.getUnsubscribe() == "test_unfoo"
        act.getResultType(objectMapper.typeFactory).toCanonical() == 'io.emeraldpay.pjc.json.BlockJson$Header'
        act.getParams() == [Hash256.from("0x5d83f66b61701da4cbd7a60137db89c69469a4f798b62aba9176ab253b423828")].toArray()

        when:
        def act2 = SubscribeCall.create(BlockJson.Header.class, "test_foo", "test_unfoo", 
                [Hash256.from("0x5d83f66b61701da4cbd7a60137db89c69469a4f798b62aba9176ab253b423828")])
        then:
        act == act2
    }

    def "Create with java type"() {
        when:
        def act = SubscribeCall.create(objectMapper.typeFactory.constructType(String.class), "test_foo", "test_unfoo",)
        then:
        act.getMethod() == "test_foo"
        act.getUnsubscribe() == "test_unfoo"
        act.getResultType(objectMapper.typeFactory).toCanonical() == "java.lang.String"
        act.getParams() != null
        act.getParams().size() == 0

        when:
        def act2 = SubscribeCall.create(objectMapper.typeFactory.constructType(String.class), "test_foo", "test_unfoo", [])
        then:
        act == act2
    }

    def "Create with java type and params"() {
        when:
        def act = SubscribeCall.create(objectMapper.typeFactory.constructType(String.class), "test_foo", "test_unfoo", 1, "foo")
        then:
        act.getMethod() == "test_foo"
        act.getUnsubscribe() == "test_unfoo"
        act.getResultType(objectMapper.typeFactory).toCanonical() == "java.lang.String"
        act.getParams() == [1, "foo"].toArray()

        when:
        def act2 = SubscribeCall.create(objectMapper.typeFactory.constructType(String.class), "test_foo", "test_unfoo", 1, "foo")
        then:
        act == act2
    }

    def "Create with list java type and params"() {
        when:
        def act = SubscribeCall.create(objectMapper.typeFactory.constructCollectionLikeType(List.class, String.class), "test_foo", "test_unfoo")
        then:
        act.getMethod() == "test_foo"
        act.getUnsubscribe() == "test_unfoo"
        act.getResultType(objectMapper.typeFactory).toCanonical() == "java.util.List<java.lang.String>"
        act.getParams().size() == 0
    }

    def "Cannot create without type"() {
        when:
        SubscribeCall.create((Class)null, "test_foo", "test_unfoo")
        then:
        thrown(NullPointerException)

        when:
        SubscribeCall.create((JavaType)null, "test_foo", "test_unfoo")
        then:
        thrown(NullPointerException)
    }

    def "Cannot create without method"() {
        when:
        SubscribeCall.create(String.class, null, "test_unfoo")
        then:
        thrown(IllegalArgumentException)

        when:
        SubscribeCall.create(String.class, "", "test_unfoo")
        then:
        thrown(IllegalArgumentException)
    }

    def "Cannot create without unsubscribe method"() {
        when:
        SubscribeCall.create(String.class, "test_foo", null)
        then:
        thrown(IllegalArgumentException)

        when:
        SubscribeCall.create(String.class, "test_foo", "")
        then:
        thrown(IllegalArgumentException)
    }

    def "Same calls are equal"() {
        when:
        def a = SubscribeCall.create(Integer.class, "test_foo", "test_unfoo", 1, "foo")
        def b = SubscribeCall.create(Integer.class, "test_foo", "test_unfoo", 1, "foo")
        then:
        a == b
        a.hashCode() == b.hashCode()
    }

    def "Different calls are not equal"() {
        when:
        def a = SubscribeCall.create(Integer.class, "test_foo", "test_unfoo", 1, "foo")
        def b = SubscribeCall.create(Integer.class, "test_foo", "test_unfoo", 2, "foo")
        then:
        a != b
    }

    def "Cast to base type"() {
        when:
        def orig = SubscribeCall.create(Integer.class, "test_foo", "test_unfoo")
        def act = orig.cast(Number.class)
        then:
        act != null
    }

    def "Cannot cast to another type"() {
        when:
        def orig = SubscribeCall.create(Integer.class, "test_foo", "test_unfoo")
        def act = orig.cast(Long.class)
        then:
        thrown(ClassCastException)
    }

    def "Casting doesnt change actual type"() {
        when:
        def orig = SubscribeCall.create(Integer.class, "test_foo", "test_unfoo")
        def act = orig.cast(Object.class)
        then:
        act != null
        act.getResultType(objectMapper.typeFactory).toCanonical() == "java.lang.Integer"

        when:
        act = act.cast(Number.class)
        then:
        act != null
        act.getResultType(objectMapper.typeFactory).toCanonical() == "java.lang.Integer"
    }

    def "Cast java type to class"() {
        when:
        def orig = SubscribeCall.create(objectMapper.typeFactory.constructType(Integer.class), "test_foo", "test_unfoo")
        def act = orig.cast(Number.class)
        then:
        act != null
    }

    def "Cannot cast java type to class when different"() {
        when:
        def orig = SubscribeCall.create(objectMapper.typeFactory.constructType(Integer.class), "test_foo", "test_unfoo")
        def act = orig.cast(String.class)
        then:
        thrown(ClassCastException)
    }
}
