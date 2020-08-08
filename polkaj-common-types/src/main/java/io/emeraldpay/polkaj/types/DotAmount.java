package io.emeraldpay.polkaj.types;

import io.emeraldpay.polkaj.types.Units.Unit;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Objects;

/**
 * Amount value, in DOTs
 *
 * @see <a href="https://wiki.polkadot.network/docs/en/learn-DOT">https://wiki.polkadot.network/docs/en/learn-DOT</a>
 */
public class DotAmount implements Comparable<DotAmount>{

    /**
     * Standard units for DOT token
     *
     * Planck -&gt; Point -&gt; Microdot -&gt; Millidot -&gt; Dot
     */
    public static final Units Polkadots = new Units(
            Units.Planck,
            Units.Point,
            Units.Microdot,
            Units.Millidot,
            Units.Dot
    );

    /**
     * Kusama testnet units
     *
     * Planck -&gt;  Point -&gt; MicroKSM -&gt; MilliKSM -&gt; KSM
     */
    public static final Units Kusamas = new Units(
            new Unit("Planck", 0),
            new Unit("Point", 3),
            new Unit("MicroKSM", "uKSM", 6),
            new Unit("MilliKSM", "mKSM", 9),
            new Unit("KSM", 12)
    );

    /**
     * Zero Dots
     */
    public static final DotAmount ZERO = new DotAmount(BigInteger.ZERO);

    private static final Unit DOT_UNIT = Polkadots.getMain();
    private static final BigInteger DOT_MULTIPLIER = DOT_UNIT.getMultiplier();
    private static final BigDecimal DOT_MULTIPLIER_DECIMAL = new BigDecimal(DOT_MULTIPLIER);

    private final BigInteger value;
    private final Units units;

    /**
     * Create a standard DOT amount for the specified amount
     *
     * @param value amount
     */
    public DotAmount(BigInteger value) {
        this(value, Polkadots);
    }

    /**
     * Create a DOT amount with custom units
     *
     * @see DotAmount#Polkadots
     * @see DotAmount#Kusamas
     *
     * @param value amount
     * @param units custom units
     */
    public DotAmount(BigInteger value, Units units) {
        this.value = value;
        this.units = units;
    }

    public static DotAmount fromPlancks(long amount) {
        return new DotAmount(BigInteger.valueOf(amount), Polkadots);
    }

    public static DotAmount fromPlancks(String amount) {
        return new DotAmount(new BigInteger(amount), Polkadots);
    }

    public static DotAmount fromDots(long amount) {
        return new DotAmount(
                BigInteger.valueOf(amount)
                        .multiply(DOT_MULTIPLIER)
                , Polkadots);
    }

    public static DotAmount fromDots(double amount) {
        return new DotAmount(
                BigDecimal.valueOf(amount)
                        .multiply(DOT_MULTIPLIER_DECIMAL)
                        .toBigInteger()
                , Polkadots);
    }

    public boolean isSame(DotAmount another) {
        return units.equals(another.units);
    }

    protected void requireSame(DotAmount another) {
        if (!isSame(another)) {
            throw new IllegalStateException("Amounts belong to different networks");
        }
    }

    public DotAmount add(DotAmount amount) {
        requireSame(amount);
        return new DotAmount(this.value.add(amount.value), this.units);
    }

    public DotAmount substract(DotAmount amount) {
        requireSame(amount);
        return new DotAmount(this.value.subtract(amount.value), this.units);
    }

    public DotAmount multiply(long n) {
        return new DotAmount(this.value.multiply(BigInteger.valueOf(n)), this.units);
    }

    public DotAmount divide(long n) {
        return new DotAmount(this.value.divide(BigInteger.valueOf(n)), this.units);
    }

    public BigInteger getValue() {
        return value;
    }

    public BigDecimal getValue(Unit unit) {
        if (unit.getDecimals() == 0) {
            return new BigDecimal(value);
        }
        return new BigDecimal(value)
                .divide(new BigDecimal(unit.getMultiplier()), MathContext.DECIMAL64);
    }

    public Units getUnits() {
        return units;
    }

    /**
     * Finds a minimal unit for which a value represented in that unit has a whole part.
     * For example for 0.100 Dots the unit is Millidot (100 Millidots), for 0.000075 its Microdot (75 Microdots), and so on.
     *
     * @return a minimal unit
     */
    public Unit getMinimalUnit() {
        return getMinimalUnit(getUnits().getUnits()[0]);
    }

    /**
     * Finds a minimal unit, down to specified limit, for which a value represented in that unit has a whole part.
     * For example for 0.100 Dots the unit is Millidot (100 Millidots), for 0.000075 its Microdot (75 Microdots), and so on.
     *
     * @param limit the limit that stop further decrease of the unit
     * @return a minimal unit
     */
    public Unit getMinimalUnit(Unit limit) {
        Unit[] units = getUnits().getUnits();
        for (int i = units.length-1; i > 0; i--) {
            Unit unit = units[i];
            if (unit == limit) {
                return unit;
            }
            if (value.compareTo(unit.getMultiplier()) >= 0) {
                return unit;
            }
        }
        return units[0];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DotAmount)) return false;
        DotAmount dotAmount = (DotAmount) o;
        return value.equals(dotAmount.value) &&
                units.equals(dotAmount.units);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, units);
    }

    @Override
    public String toString() {
        return value.toString() + " " + units.getMain().getShortName();
    }

    @Override
    public int compareTo(DotAmount o) {
        if (units != o.units) {
            return units.getMain().getName().compareTo(o.units.getMain().toString());
        }
        return value.compareTo(o.value);
    }
}
