package io.emeraldpay.pjc.types;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * DotAmount formatter, to represent amounts in human friendly way.
 * <br/>
 * Usage:
 * <code><pre>
 * DotAmountFormatter fmt = DotAmountFormatter.newBuilder()
 *                 .usingMinimalUnit() // specify to display in minimal unit that covers whole part of the amount
 *                 .fullNumber("#,##0.00") // put number, using format of the decimal number
 *                 .exactString(" ") // add space
 *                 .shortUnit() // short name of the unit (Millidot -> mDOT)
 *                 .build();
 *
 * // prints "56.79 uDOT"
 * System.out.println(
 *    fmt.format(DotAmount.fromPlanks(56_789_000))
 * );
 * </pre></code>
 *
 * The formatter built above is standard and can be build from <code>DotAmountFormatter.autoShortFormatter</code>
 *
 * @see DotAmount
 */
public class DotAmountFormatter {

    private final Formatter[] formatters;

    private static DotAmountFormatter full;
    private static DotAmountFormatter auto;
    private static DotAmountFormatter autoShort;

    public DotAmountFormatter(Formatter[] formatters) {
        this.formatters = formatters;
    }

    /**
     * Standard formatter that always prints a full amount in a smallest unit (<code>123456789000 Planck</code>)
     *
     * @return formatter
     */
    public static DotAmountFormatter fullFormatter() {
        if (full == null) {
            full = newBuilder()
                    .fullNumber()
                    .exactString(" ")
                    .fullUnit()
                    .build();
        }
        return full;
    }

    /**
     * Standard formatter that prints amount with an optimal unit (56,789,000 Planck as <code>56.79 Microdot</code>)
     *
     * @return formatter
     */
    public static DotAmountFormatter autoFormatter() {
        if (auto == null) {
            auto = newBuilder()
                    .usingMinimalUnit()
                    .fullNumber("#,##0.00")
                    .exactString(" ")
                    .fullUnit()
                    .build();
        }
        return auto;
    }

    /**
     * Standard formatter that prints amount with an optimal unit using a short notation (56,789,000 Planck as <code>56.79 mDOT</code>)
     *
     * @return formatter
     */
    public static DotAmountFormatter autoShortFormatter() {
        if (autoShort == null) {
            autoShort = newBuilder()
                    .usingMinimalUnit()
                    .fullNumber("#,##0.00")
                    .exactString(" ")
                    .shortUnit()
                    .build();
        }
        return autoShort;
    }

    /**
     * Apply formatter to the value
     *
     * @param value amount
     * @return formatted amount
     */
    public String format(DotAmount value) {
        Context ctx = new Context(value);
        for (Formatter formatter: formatters) {
            formatter.apply(ctx);
        }
        return ctx.buffer.toString();
    }

    /**
     * Start a new formatter configuration builder
     *
     * @return configuration builder
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Formatting context
     */
    public static class Context {
        private final DotAmount source;
        private final StringBuilder buffer;

        private BigDecimal value;
        private Units.Unit unit;

        public Context(DotAmount source) {
            this.source = source;
            this.value = new BigDecimal(source.getValue());
            this.unit = source.getUnits().getBase();
            this.buffer = new StringBuilder();
        }

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }

        public Units.Unit getUnit() {
            return unit;
        }

        public void setUnit(Units.Unit unit) {
            this.unit = unit;
        }

        public StringBuilder getBuffer() {
            return buffer;
        }

        public DotAmount getSource() {
            return source;
        }
    }

    /**
     * Formatter step. An implementation can manipulate current value (BigDecimal) and corresponding Unit, or modify
     * internal StringBuilder.
     */
    interface Formatter {
        void apply(Context ctx);
    }

    public static class Builder {
        private final List<Formatter> formatters = new ArrayList<>();

        /**
         * Append custom formatter
         * @param formatter custom formatter
         * @return this builder
         */
        public Builder append(Formatter formatter) {
            formatters.add(formatter);
            return this;
        }

        /**
         * Appends specified string when the formatter called (i.e. a space separator)
         * @param s string to append
         * @return this builder
         */
        public Builder exactString(String s) {
            return this.append(new ExactString(s));
        }

        /**
         * Appends the whole number as string, without any format
         * @return this builder
         */
        public Builder fullNumber() {
            return this.append(new FullNumber());
        }

        /**
         * Appends the whole number as string, with specified format which should be supported by DecimalFormat
         * @return this builder
         * @see DecimalFormat
         */
        public Builder fullNumber(String pattern) {
            return this.append(new FullNumber(new DecimalFormat(pattern)));
        }

        /**
         * Specify which unit should be used for formatting. The amount will be converted.
         * @return this builder
         */
        public Builder usingUnit(Units.Unit unit) {
            return this.append(new UsingUnit(unit));
        }

        /**
         * Configure to find an optimal unit for formatting, limiting to the specified one as the smalles acceptable.
         * @return this builder
         */
        public Builder usingMinimalUnit(Units.Unit unit) {
            return this.append(new UsingMinimalUnit(unit));
        }

        /**
         * Configure to find an optimal unit for formatting
         * @return this builder
         */
        public Builder usingMinimalUnit() {
            return this.append(new UsingMinimalUnit(null));
        }

        /**
         * Appends a full unit name (i.e. Millidot)
         * @return this builder
         */
        public Builder fullUnit() {
            return this.append(new FullUnit());
        }

        /**
         * Appends a short unit name (i.e. uDOT)
         * @return this builder
         */
        public Builder shortUnit() {
            return this.append(new ShortUnit());
        }

        /**
         * Finalize the formatter
         * @return formatter
         */
        public DotAmountFormatter build() {
            return new DotAmountFormatter(formatters.toArray(new Formatter[0]));
        }
    }

    static class ExactString implements Formatter {

        private final String string;

        public ExactString(String string) {
            this.string = string;
        }

        @Override
        public void apply(Context ctx) {
            ctx.getBuffer().append(string);
        }
    }

    static class FullNumber implements Formatter {

        private final DecimalFormat format;

        public FullNumber() {
            this(new DecimalFormat("0"));
        }

        public FullNumber(DecimalFormat format) {
            this.format = format;
        }

        @Override
        public void apply(Context ctx) {
            ctx.getBuffer().append(format.format(ctx.getValue()));
        }
    }

    static class FullUnit implements Formatter {

        @Override
        public void apply(Context ctx) {
            ctx.getBuffer().append(ctx.unit.getName());
        }
    }

    static class ShortUnit implements Formatter {

        @Override
        public void apply(Context ctx) {
            ctx.getBuffer().append(ctx.getUnit().getShortName());
        }
    }

    static class UsingUnit implements Formatter {

        private final Units.Unit unit;

        public UsingUnit(Units.Unit unit) {
            this.unit = unit;
        }

        @Override
        public void apply(Context ctx) {
            ctx.setUnit(unit);
            ctx.setValue(ctx.getSource().getValue(unit));
        }
    }

    static class UsingMinimalUnit implements Formatter {

        private final Units.Unit unit;

        public UsingMinimalUnit(Units.Unit unit) {
            this.unit = unit;
        }

        @Override
        public void apply(Context ctx) {
            if (this.unit != null) {
                ctx.setUnit(ctx.getSource().getMinimalUnit(this.unit));
            } else {
                ctx.setUnit(ctx.getSource().getMinimalUnit());
            }
            ctx.setValue(ctx.getSource().getValue(ctx.unit));
        }
    }
}
