package io.emeraldpay.polkaj.types

import spock.lang.Specification

class UnitsSpec extends Specification {

    def "Creates"() {
        when:
        def act = new Units(
                new Units.Unit("Cent", 1),
                new Units.Unit("Dollar", 100)
        )

        then:
        act.units.length == 2
        act.units[0].decimals == 1
        act.units[1].decimals == 100
    }

    def "To string is the full name"() {
        when:
        def act = new Units(
                new Units.Unit("Cent", 1),
                new Units.Unit("Dollar", 100)
        )

        then:
        act.toString() == "Dollar"
        act.main.toString() == "Dollar"
    }

    def "Doesn't create unordered"() {
        when:
        new Units(
                new Units.Unit("Dollar", 100),
                new Units.Unit("Cent", 1)
        )

        then:
        thrown(IllegalArgumentException)
    }

    def "Doesn't create empty"() {
        when:
        new Units()

        then:
        thrown(IllegalArgumentException)
    }

    def "Doesn't create null"() {
        when:
        new Units(null)

        then:
        thrown(NullPointerException)
    }

    def "Main unit is the largest"() {
        setup:
        def units = new Units(
                new Units.Unit("Planck", 0),
                new Units.Unit("Point", 3),
                new Units.Unit("Microdot", "uDOT", 6),
                new Units.Unit("Millidot", "mDOT", 9),
                new Units.Unit("Dot", "DOT", 12)
        )
        when:
        def act = units.main
        then:
        act.name == "Dot"
    }

    def "Multiplier is amount of decimals"() {
        when:
        def act = new Units.Unit("Test", 0).multiplier
        then:
        act == 1

        when:
        act = new Units.Unit("Test", 6).multiplier
        then:
        act == 1_000_000

        when:
        act = new Units.Unit("Test", 12).multiplier
        then:
        act == 1_000_000_000_000
    }

    def "Same hashcode for same main unit"() {
        when:
        def units1 = new Units(
                new Units.Unit("Planck", 0),
                new Units.Unit("Point", 3),
                new Units.Unit("Microdot", "uDOT", 6),
                new Units.Unit("Millidot", "mDOT", 9),
                new Units.Unit("Dot", "DOT", 12)
        )
        def units2 = new Units(
                new Units.Unit("Planck", 0),
                new Units.Unit("Dot", "DOT", 12)
        )
        then:
        units1.hashCode() == units2.hashCode()
    }

    def "Missing units are not equal to full"() {
        when:
        def units1 = new Units(
                new Units.Unit("Planck", 0),
                new Units.Unit("Point", 3),
                new Units.Unit("Microdot", "uDOT", 6),
                new Units.Unit("Millidot", "mDOT", 9),
                new Units.Unit("Dot", "DOT", 12)
        )
        def units2 = new Units(
                new Units.Unit("Planck", 0),
                new Units.Unit("Dot", "DOT", 12)
        )
        then:
        !units1.equals(units2)
    }

    def "Same units are equal"() {
        when:
        def units1 = new Units(
                new Units.Unit("Planck", 0),
                new Units.Unit("Point", 3),
                new Units.Unit("Microdot", "uDOT", 6),
                new Units.Unit("Millidot", "mDOT", 9),
                new Units.Unit("Dot", "DOT", 12)
        )
        def units2 = new Units(
                new Units.Unit("Planck", 0),
                new Units.Unit("Point", 3),
                new Units.Unit("Microdot", "uDOT", 6),
                new Units.Unit("Millidot", "mDOT", 9),
                new Units.Unit("Dot", "DOT", 12)
        )
        then:
        units1.equals(units2)
    }
}
