package io.emeraldpay.polkaj.types

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

    def "Create from string"() {
        when:
        def act = DotAmount.fromPlancks("123456789")
        then:
        act.value == 123456789
        act.units.main.name == "Dot"
    }

    def "Create from dots"() {
        when:
        def act = DotAmount.fromDots(123.456789)
        then:
        act.value.toString() == "1234567890000"
        act.units.main.name == "Dot"

        when:
        act = DotAmount.fromDots(123L)
        then:
        act.value.toString() == "1230000000000"
        act.units.main.name == "Dot"
    }

    def "Addition works"() {
        when:
        def act = DotAmount.fromDots(10)
                .add(DotAmount.fromDots(5.123))
                .add(DotAmount.fromDots(0.000456))

        then:
        act.value.toString() == "151234560000"
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
        act.value.toString() == "48770000000"
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
        act.value.toString() == "202500000000"
    }

    def "Division works"() {
        when:
        def act = DotAmount.fromDots(20.250)
                .divide(2)
        then:
        act.value.toString() == "101250000000"
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
        0.1             |    "1000000000 DOT"
        100.0           | "1000000000000 DOT"
        0.1234567891    |    "1234567891 DOT"
    }

    def "Same amounts are equal"() {
        when:
        def amount1 = DotAmount.fromDots(10)
        def amount2 = new DotAmount(BigInteger.valueOf(100000000000))

        then:
        amount1.equals(amount2)
    }

    def "Different amounts are not equal"() {
        when:
        def amount1 = DotAmount.fromDots(10)
        def amount2 = new DotAmount(BigInteger.valueOf(500000000000))

        then:
        !amount1.equals(amount2)
    }

    def "Same amounts but different units are not equal"() {
        when:
        def amount1 = DotAmount.fromDots(10)
        def amount2 = new DotAmount(BigInteger.valueOf(100000000000), DotAmount.Kusamas)

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
        def amount2 = new DotAmount(BigInteger.valueOf(100000000000))

        then:
        amount1.hashCode() == amount2.hashCode()
    }

    def "Different hashcodes for different values"() {
        when:
        def amount1 = DotAmount.fromDots(50)
        def amount2 = new DotAmount(BigInteger.valueOf(100000000000))

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
        1000        | "Planck"
        5000        | "Planck"
        9999        | "Planck"

        10_000       | "Microdot"
        100_000      | "Microdot"
        1_000_000    | "Microdot"
        5_000_000    | "Microdot"

        10_000_000     | "Millidot"
        50_000_000     | "Millidot"
        99_000_000     | "Millidot"

        10_000_000_000   | "Dot"
        50_000_000_000   | "Dot"
        100_000_000_000  | "Dot"
    }

    def "Get minimal with limit"() {
        when:
        def act = DotAmount
                .fromPlancks(10_000) //ideal is micro
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

    def "Get value in micro"() {
        expect:
        DotAmount.fromPlancks(amount)
                .getValue(Units.Microdot)
                .toString() == result
        where:
        amount              | result
        10_000_000_000_000  | "1000000000"
        12_345_678_123_456  | "1234567812.3456"
            20_900_800_700  |    "2090080.07"
                30_900_800  |       "3090.08"
                    40_900  |          "4.09"
                        50  |          "0.005"
                        00  |          "0"
    }

    def "Get value in milli"() {
        expect:
        DotAmount.fromPlancks(amount)
                .getValue(Units.Millidot)
                .toPlainString() == result
        where:
        amount              | result
        10_000_000_000_000  | "1000000"
        12_345_678_123_456  | "1234567.8123456"
            20_900_800_700  |    "2090.08007"
                30_900_800  |       "3.09008"
                    40_900  |       "0.00409"
                        50  |       "0.000005"
                        00  |       "0"
    }

    def "Get value in dot"() {
        expect:
        DotAmount.fromPlancks(amount)
                .getValue(Units.Dot)
                .toPlainString() == result
        where:
        amount              | result
        10_000_000_000_000  | "1000"
        12_345_678_123_456  | "1234.5678123456"
            20_900_800_700  |    "2.09008007"
                30_900_800  |    "0.00309008"
                    40_900  |    "0.00000409"
                        50  |    "0.000000005"
                        00  |    "0"
    }

    def "Uses denomination examples"() {
        when:
        def act = DotAmount.fromPlancks(1_000_000_000_000)
                .getValue(Units.Dot)
        then:
        act == BigDecimal.valueOf(100)
    }
}
