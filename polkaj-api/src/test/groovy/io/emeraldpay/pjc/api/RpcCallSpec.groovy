package io.emeraldpay.pjc.api

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import io.emeraldpay.pjc.json.BlockJson
import io.emeraldpay.pjc.json.jackson.PolkadotModule
import io.emeraldpay.pjc.types.Hash256
import spock.lang.Specification

class RpcCallSpec extends Specification {

    ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new PolkadotModule())

    def "Create with class"() {
        when:
        def act = RpcCall.create(String.class, "test_foo")
        then:
        act.getMethod() == "test_foo"
        act.getResultType(objectMapper.typeFactory).toCanonical() == "java.lang.String"
        act.getParams() != null
        act.getParams().size() == 0
    }

    def "Create with class and params"() {
        when:
        def act = RpcCall.create(String.class, "test_foo", 1, "foo")
        then:
        act.getMethod() == "test_foo"
        act.getResultType(objectMapper.typeFactory).toCanonical() == "java.lang.String"
        act.getParams() == [1, "foo"].toArray()

        when:
        def act2 = RpcCall.create(String.class, "test_foo", [1, "foo"])
        then:
        act == act2
    }

    def "Create with custom class and params"() {
        when:
        def act = RpcCall.create(BlockJson.Header.class, "test_foo", Hash256.from("0x5d83f66b61701da4cbd7a60137db89c69469a4f798b62aba9176ab253b423828"))
        then:
        act.getMethod() == "test_foo"
        act.getResultType(objectMapper.typeFactory).toCanonical() == 'io.emeraldpay.pjc.json.BlockJson$Header'
        act.getParams() == [Hash256.from("0x5d83f66b61701da4cbd7a60137db89c69469a4f798b62aba9176ab253b423828")].toArray()
    }

    def "Create with java type"() {
        when:
        def act = RpcCall.create(objectMapper.typeFactory.constructType(String.class), "test_foo")
        then:
        act.getMethod() == "test_foo"
        act.getResultType(objectMapper.typeFactory).toCanonical() == "java.lang.String"
        act.getParams() != null
        act.getParams().size() == 0

        when:
        def act2 = RpcCall.create(objectMapper.typeFactory.constructType(String.class), "test_foo", [])
        then:
        act == act2
    }

    def "Create with java type and params"() {
        when:
        def act = RpcCall.create(objectMapper.typeFactory.constructType(String.class), "test_foo", 1, "foo")
        then:
        act.getMethod() == "test_foo"
        act.getResultType(objectMapper.typeFactory).toCanonical() == "java.lang.String"
        act.getParams() == [1, "foo"].toArray()

        when:
        def act2 = RpcCall.create(objectMapper.typeFactory.constructType(String.class), "test_foo", 1, "foo")
        then:
        act == act2
    }

    def "Create with list java type and params"() {
        when:
        def act = RpcCall.create(objectMapper.typeFactory.constructCollectionLikeType(List.class, String.class), "test_foo")
        then:
        act.getMethod() == "test_foo"
        act.getResultType(objectMapper.typeFactory).toCanonical() == "java.util.List<java.lang.String>"
        act.getParams().size() == 0
    }

    def "Cannot create without type"() {
        when:
        RpcCall.create((Class)null, "test_foo")
        then:
        thrown(NullPointerException)

        when:
        RpcCall.create((JavaType)null, "test_foo")
        then:
        thrown(NullPointerException)
    }

    def "Cannot create without method"() {
        when:
        RpcCall.create(String.class, null)
        then:
        thrown(IllegalArgumentException)

        when:
        RpcCall.create(String.class, "")
        then:
        thrown(IllegalArgumentException)
    }

    def "Doesnt need type factory if java type is set"() {
        when:
        def act = RpcCall.create(objectMapper.typeFactory.constructType(String.class), "test_foo", 1, "foo")
        then:
        act.getResultType(null).toCanonical() == "java.lang.String"
    }

    def "Fail to get type without factory if type is class"() {
        when:
        def act = RpcCall.create(String.class, "test_foo", 1, "foo")
        act.getResultType(null)
        then:
        thrown(NullPointerException)
    }

    def "Cast to base type"() {
        when:
        def orig = RpcCall.create(Integer.class, "test_foo")
        def act = orig.cast(Number.class)
        then:
        act != null
    }

    def "Cannot cast to another type"() {
        when:
        def orig = RpcCall.create(Integer.class, "test_foo")
        def act = orig.cast(Long.class)
        then:
        thrown(ClassCastException)
    }

    def "Casting doesnt change actual type"() {
        when:
        def orig = RpcCall.create(Integer.class, "test_foo")
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
        def orig = RpcCall.create(objectMapper.typeFactory.constructType(Integer.class), "test_foo")
        def act = orig.cast(Number.class)
        then:
        act != null
    }

    def "Convert to list result"() {
        when:
        def act = RpcCall.create(Integer.class, "test_foo").expectList()
        then:
        act != null
        act.getResultType(objectMapper.typeFactory).toCanonical() == "java.util.List<java.lang.Integer>"
    }

    def "Same calls are equal"() {
        when:
        def a = RpcCall.create(Integer.class, "test_foo", 1, "foo")
        def b = RpcCall.create(Integer.class, "test_foo", 1, "foo")
        then:
        a == b
        a.hashCode() == b.hashCode()
    }

    def "Different calls are not equal"() {
        when:
        def a = RpcCall.create(Integer.class, "test_foo", 1, "foo")
        def b = RpcCall.create(Integer.class, "test_foo", 2, "foo")
        then:
        a != b
    }

}
