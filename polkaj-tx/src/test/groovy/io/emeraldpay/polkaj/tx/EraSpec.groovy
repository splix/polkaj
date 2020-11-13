package io.emeraldpay.polkaj.tx

import org.apache.commons.codec.binary.Hex
import spock.lang.Specification
import spock.lang.Unroll

class EraSpec extends Specification {

    def "Fail to decode negative"() {
        when:
        Era.decode(-1)
        then:
        thrown(IllegalArgumentException)
    }

    def "Fail to decode more than two bytes"() {
        when:
        Era.decode(0x1ffff)
        then:
        thrown(IllegalArgumentException)
    }

    def "Decode immortal"() {
        when:
        def act = Era.decode(0)
        then:
        act instanceof Era.Immortal
        act.isImmortal()
    }

    def "Encode immortal"() {
        when:
        def act = new Era.Immortal().encode()
        then:
        Hex.encodeHexString(act) == "00"
    }

    def "Immortal to integer"() {
        when:
        def act = new Era.Immortal().toInteger()
        then:
        act == 0
    }

    def "Decode mortal"() {
        when:
        def act = Era.decode(0x9c4e)
        then:
        act instanceof Era.Mortal
        with((Era.Mortal)act) {
            period == 32768
            phase == 20000
        }
        !act.isImmortal()
    }

    def "Encode mortal"() {
        when:
        def act = new Era.Mortal(32768, 20000).encode()
        then:
        Hex.encodeHexString(act) == "9c4e"
    }

    def "Mortal to integer"() {
        when:
        def act = new Era.Mortal(32768, 20000).toInteger()
        then:
        act == 0x9c4e
    }

    @Unroll
    def "Immortal birth on #current"() {
        setup:
        def era = new Era.Immortal()
        expect:
        era.birth(current) == 0
        where:
        current << [6, 7, 8, 9]
    }

    @Unroll
    def "Immortal death on #current"() {
        setup:
        def era = new Era.Immortal()
        expect:
        era.death(current) == Long.MAX_VALUE
        where:
        current << [6, 7, 8, 9]
    }

    @Unroll
    def "Mortal birth on #current"() {
        setup:
        def era = new Era.Mortal(4, 6)
        expect:
        era.birth(current) == 6
        where:
        current << [6, 7, 8, 9]
    }

    @Unroll
    def "Mortal death on #current"() {
        setup:
        def era = new Era.Mortal(4, 6)
        expect:
        era.death(current) == 10
        where:
        current << [6, 7, 8, 9]
    }

    def "Create mortal for current"() {
        expect:
        def era = Era.Mortal.forCurrent(period, current)
        era.period == expPeriod
        era.phase == expPhase
        where:
        period  | current   | expPeriod | expPhase
        64      | 42        | 64        | 42
        32768   | 20000     | 32768     | 20000
        200     | 513       | 256       | 1
        2       | 1         | 4         | 1
        4       | 5         | 4         | 1
    }
}
