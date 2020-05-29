package io.emeraldpay.polkaj.scale

import spock.lang.Specification

class UnionValueSpec extends Specification {

    def "Creates"() {
        when:
        def act = new UnionValue(0, 42)
        then:
        act.index == 0
        act.value == 42
    }

    def "Error to create with negative index"() {
        when:
        new UnionValue(-1, 42)
        then:
        thrown(IllegalArgumentException)
    }

    def "Error to create with large index"() {
        when:
        new UnionValue(256, 42)
        then:
        thrown(IllegalArgumentException)
    }

}
