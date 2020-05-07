package io.emeraldpay.pjc.types

import spock.lang.Specification
import spock.lang.Unroll

class DotAmountSpec extends Specification {

    def "Create"() {
        when:
        def act = new DotAmount(BigInteger.ONE)
        then:
        act.value == 1
        act.units.main.name == "Dot"
    }

    def "Create from planck"() {
        when:
        def act = DotAmount.fromPlancks(123456789)
        then:
        act.value == 123456789
        act.units.main.name == "Dot"
    }

    def "Create from dots"() {
        when:
        def act = DotAmount.fromDots(123.456789)
        then:
        act.value.toString() == "123456789000000"
        act.units.main.name == "Dot"

        when:
        act = DotAmount.fromDots(123L)
        then:
        act.value.toString() == "123000000000000"
        act.units.main.name == "Dot"
    }

    def "Addition works"() {
        when:
        def act = DotAmount.fromDots(10)
                .add(DotAmount.fromDots(5.123))
                .add(DotAmount.fromDots(0.000456))

        then:
        act.value.toString() == "15123456000000"
    }

    def "Cannot add different units"() {
        when:
        new DotAmount(BigInteger.valueOf(1), DotAmount.Polkadots)
                .add(new DotAmount(BigInteger.valueOf(1), DotAmount.Kusamas))

        then:
        thrown(IllegalStateException)
    }

    def "Substraction works"() {
        when:
        def act = DotAmount.fromDots(10)
                .substract(DotAmount.fromDots(5.123))

        then:
        act.value.toString() == "4877000000000"
    }

    def "Cannot substract different units"() {
        when:
        new DotAmount(BigInteger.valueOf(1), DotAmount.Polkadots)
                .substract(new DotAmount(BigInteger.valueOf(1), DotAmount.Kusamas))

        then:
        thrown(IllegalStateException)
    }

    def "Multiplication works"() {
        when:
        def act = DotAmount.fromDots(10.125)
                .multiply(2)
        then:
        act.value.toString() == "20250000000000"
    }

    def "Division works"() {
        when:
        def act = DotAmount.fromDots(20.250)
                .divide(2)
        then:
        act.value.toString() == "10125000000000"
    }

    def "Cannot division by zero"() {
        when:
        DotAmount.fromDots(20.250)
                .divide(0)
        then:
        thrown(ArithmeticException)
    }

    @Unroll
    def "toString works for #dots"() {
        expect:
        DotAmount.fromDots(dots).toString() == str
        where:
        dots            | str
        0.1             |    "100000000000 DOT"
        100.0           | "100000000000000 DOT"
        0.123456789123  |    "123456789123 DOT"
    }

    def "Same amounts are equal"() {
        when:
        def amount1 = DotAmount.fromDots(10)
        def amount2 = new DotAmount(BigInteger.valueOf(10000000000000))

        then:
        amount1.equals(amount2)
    }

    def "Different amounts are not equal"() {
        when:
        def amount1 = DotAmount.fromDots(10)
        def amount2 = new DotAmount(BigInteger.valueOf(50000000000000))

        then:
        !amount1.equals(amount2)
    }

    def "Same amounts but different units are not equal"() {
        when:
        def amount1 = DotAmount.fromDots(10)
        def amount2 = new DotAmount(BigInteger.valueOf(10000000000000), DotAmount.Kusamas)

        then:
        !amount1.equals(amount2)
    }

    def "Same units gives same amounts"() {
        when:
        def amount1 = new DotAmount(BigInteger.valueOf(5), DotAmount.Kusamas)
        def amount2 = new DotAmount(BigInteger.valueOf(3), DotAmount.Kusamas)

        then:
        amount1.isSame(amount2)
    }

    def "Diff units gives diff amounts"() {
        when:
        def amount1 = new DotAmount(BigInteger.valueOf(5), DotAmount.Kusamas)
        def amount2 = new DotAmount(BigInteger.valueOf(3), DotAmount.Polkadots)

        then:
        !amount1.isSame(amount2)
    }

    def "Same hashcodes for same values"() {
        when:
        def amount1 = DotAmount.fromDots(10)
        def amount2 = new DotAmount(BigInteger.valueOf(10000000000000))

        then:
        amount1.hashCode() == amount2.hashCode()
    }

    def "Different hashcodes for different values"() {
        when:
        def amount1 = DotAmount.fromDots(50)
        def amount2 = new DotAmount(BigInteger.valueOf(10000000000000))

        then:
        amount1.hashCode() != amount2.hashCode()
    }

    def "Sorted by amount"() {
        when:
        def amount1 = DotAmount.fromDots(1)
        def amount2 = DotAmount.fromDots(1.1)
        def amount3 = DotAmount.fromDots(5)
        def amount4 = DotAmount.fromDots(10.125)

        then:
        amount1 < amount2
        amount2 < amount3
        amount3 < amount4

        amount2 > amount1
        amount2 < amount3
        amount2 < amount4

        amount3 > amount1
        amount3 > amount2
        amount3 < amount4

        amount4 > amount1
        amount4 > amount2
        amount4 > amount3

        amount1.compareTo(amount1) == 0
        amount2.compareTo(amount2) == 0
    }

    def "For different units sort by unit name, then amount"() {
        when:
        //DOT comes before KSM
        def act = [
                new DotAmount(BigInteger.valueOf(1), DotAmount.Polkadots),
                new DotAmount(BigInteger.valueOf(2), DotAmount.Kusamas),
                new DotAmount(BigInteger.valueOf(3), DotAmount.Polkadots),
                new DotAmount(BigInteger.valueOf(4), DotAmount.Kusamas),
        ]
        Collections.sort(act)

        then:
        act[0].value == 1
        act[1].value == 3
        act[2].value == 2
        act[3].value == 4
    }

    def "Get minimal"() {
        expect:
        DotAmount.fromPlancks(amount).minimalUnit.name == unit
        where:
        amount      | unit
        1           | "Planck"
        5           | "Planck"
        50          | "Planck"
        100         | "Planck"
        500         | "Planck"
        999         | "Planck"

        1000        | "Point"
        5000        | "Point"
        10000       | "Point"
        50000       | "Point"
        999_999     | "Point"

        1_000_000    | "Microdot"
        5_000_000    | "Microdot"

        1_000_000_000    | "Millidot"
        5_000_000_000    | "Millidot"

        1_000_000_000_000   | "Dot"
        5_000_000_000_000   | "Dot"
        10_000_000_000_000  | "Dot"
    }

    def "Get minimal with limit"() {
        when:
        def act = DotAmount
                .fromPlancks(1_000_000) //ideal is micro
                .getMinimalUnit(Units.Millidot) //but should stop at milli
        then:
        act == Units.Millidot
    }

    def "Get value in planks"() {
        expect:
        DotAmount.fromPlancks(amount)
                .getValue(Units.Planck)
                .toString() == result
        where:
        amount              | result
        10_000_000_000_000  | "10000000000000"
        12_345_678_123_456  | "12345678123456"
        10_000_000_000      | "10000000000"
        10_000_000          | "10000000"
        10_000              | "10000"
        10                  | "10"
        0                   | "0"
    }

    def "Get value in points"() {
        expect:
        DotAmount.fromPlancks(amount)
                .getValue(Units.Point)
                .toString() == result
        where:
        amount              | result
        10_000_000_000_000  | "10000000000"
        12_345_678_123_456  | "12345678123.456"
            20_900_800_700  |    "20900800.7"
                30_900_800  |       "30900.8"
                    40_900  |          "40.9"
                        50  |           "0.05"
                        00  |           "0"
    }

    def "Get value in micro"() {
        expect:
        DotAmount.fromPlancks(amount)
                .getValue(Units.Microdot)
                .toString() == result
        where:
        amount              | result
        10_000_000_000_000  | "10000000"
        12_345_678_123_456  | "12345678.123456"
            20_900_800_700  |    "20900.8007"
                30_900_800  |       "30.9008"
                    40_900  |        "0.0409"
                        50  |        "0.00005"
                        00  |        "0"
    }

    def "Get value in milli"() {
        expect:
        DotAmount.fromPlancks(amount)
                .getValue(Units.Millidot)
                .toPlainString() == result
        where:
        amount              | result
        10_000_000_000_000  | "10000"
        12_345_678_123_456  | "12345.678123456"
            20_900_800_700  |    "20.9008007"
                30_900_800  |     "0.0309008"
                    40_900  |     "0.0000409"
                        50  |     "0.00000005"
                        00  |     "0"
    }

    def "Get value in dot"() {
        expect:
        DotAmount.fromPlancks(amount)
                .getValue(Units.Dot)
                .toPlainString() == result
        where:
        amount              | result
        10_000_000_000_000  | "10"
        12_345_678_123_456  | "12.345678123456"
            20_900_800_700  |  "0.0209008007"
                30_900_800  |  "0.0000309008"
                    40_900  |  "0.0000000409"
                        50  |  "0.00000000005"
                        00  |  "0"
    }
}
