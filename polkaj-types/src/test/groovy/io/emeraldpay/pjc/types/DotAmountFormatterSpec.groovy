package io.emeraldpay.pjc.types

import spock.lang.Specification

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class DotAmountFormatterSpec extends Specification {

    static DecimalFormat localeFormat = new DecimalFormat()
    static DecimalFormatSymbols localeSymbols = localeFormat.decimalFormatSymbols

    DotAmount amount1 = DotAmount.fromPlancks(5_123_456_789_000) // 5 dot
    DotAmount amount2 = DotAmount.fromPlancks(  123_456_789_000) // 123 Milli
    DotAmount amount3 = DotAmount.fromPlancks(   23_456_789_000)
    DotAmount amount4 = DotAmount.fromPlancks(    3_456_789_000)
    DotAmount amount5 = DotAmount.fromPlancks(      456_789_000) // 456 Micro
    DotAmount amount6 = DotAmount.fromPlancks(       56_789_000)
    DotAmount amount7 = DotAmount.fromPlancks(        6_789_000)
    DotAmount amount8 = DotAmount.fromPlancks(          789_000) // 789 Point
    DotAmount amount9 = DotAmount.fromPlancks(           89_000)

    // update test data to conform the current local.
    // the test data is using EN standard for format, like "1,000.00", but on other locales it may be "1 000,00"
    static String forLocale(String exp) {
        exp.toCharArray().collect { c ->
            if (c == ".") {
                return localeSymbols.decimalSeparator
            } else if (c == ",") {
                return localeSymbols.groupingSeparator
            } else {
                return c
            }
        }.join("")
    }

    def "Simple"() {
        setup:
        def fmt = DotAmountFormatter.newBuilder()
                .fullNumber()
                .exactString(" ")
                .fullUnit()
                .build()
        when:
        def act = fmt.format(amount1)
        then:
        act == "5123456789000 Planck"
    }

    def "Standard full"() {
        expect:
        DotAmount value = DotAmount.fromPlancks(amount)
        DotAmountFormatter.fullFormatter().format(value) == exp
        where:
        amount              | exp
        5_123_456_789_000   | "5123456789000 Planck"
               56_789_000   | "56789000 Planck"
    }

    def "Standard auto"() {
        expect:
        DotAmount value = DotAmount.fromPlancks(amount)
        DotAmountFormatter.autoFormatter().format(value) == exp
        where:
        amount              | exp
        5_123_456_789_000   | forLocale("5.12 Dot")
               56_789_000   | forLocale("56.79 Microdot")
    }

    def "Standard short"() {
        expect:
        DotAmount value = DotAmount.fromPlancks(amount)
        DotAmountFormatter.autoShortFormatter().format(value) == exp
        where:
        amount              | exp
        5_123_456_789_000   | forLocale("5.12 DOT")
               56_789_000   | forLocale("56.79 uDOT")
    }

    def "With group separator"() {
        setup:
        def fmt = DotAmountFormatter.newBuilder()
                .fullNumber("#,##0")
                .exactString(" ")
                .fullUnit()
                .build()
        when:
        def act = fmt.format(amount1)
        then:
        act == forLocale("5,123,456,789,000 Planck")
    }

    def "With decimal part"() {
        setup:
        def fmt = DotAmountFormatter.newBuilder()
                .usingUnit(Units.Dot)
                .fullNumber("#,##0.00")
                .exactString(" ")
                .fullUnit()
                .build()
        when:
        def act = fmt.format(amount1)
        then:
        act == forLocale("5.12 Dot")
    }

    def "Converted with decimal part"() {
        setup:
        def fmt = DotAmountFormatter.newBuilder()
                .usingUnit(Units.Millidot)
                .fullNumber("#,##0.00")
                .exactString(" ")
                .fullUnit()
                .build()
        when:
        def act = fmt.format(amount2)
        then:
        act == forLocale("123.46 Millidot")
    }

    def "Converted large with decimal part"() {
        setup:
        def fmt = DotAmountFormatter.newBuilder()
                .usingUnit(Units.Point)
                .fullNumber("#,##0.00")
                .exactString(" ")
                .fullUnit()
                .build()
        when:
        def act = fmt.format(amount1)
        then:
        act == forLocale("5,123,456,789.00 Point")
    }

    def "Using short unit"() {
        setup:
        def fmt = DotAmountFormatter.newBuilder()
                .usingUnit(Units.Millidot)
                .fullNumber("#,##0.000")
                .exactString(" ")
                .shortUnit()
                .build()
        when:
        def act = fmt.format(amount2)
        then:
        act == forLocale("123.457 mDOT")
    }

    def "Using auto unit"() {
        setup:
        def fmt = DotAmountFormatter.newBuilder()
                .usingMinimalUnit()
                .fullNumber("#,##0.00")
                .exactString(" ")
                .shortUnit()
                .build()
        when:
        def act = fmt.format(amount5)
        then:
        act == forLocale("456.79 uDOT")
    }

    def "Using auto with limit unit"() {
        setup:
        def fmt = DotAmountFormatter.newBuilder()
                .usingMinimalUnit(Units.Millidot)
                .fullNumber("#,##0.000000")
                .exactString(" ")
                .shortUnit()
                .build()
        when:
        def act = fmt.format(amount8)
        then:
        act == forLocale("0.000789 mDOT")
    }
}
