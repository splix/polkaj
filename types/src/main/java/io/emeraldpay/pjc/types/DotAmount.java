package io.emeraldpay.pjc.types;

import io.emeraldpay.pjc.types.Units.Unit;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Amount value, in DOTs
 *
 * @link https://wiki.polkadot.network/docs/en/learn-DOT
 */
public class DotAmount implements Comparable<DotAmount>{

    /**
     * Standard units for DOT token
     *
     * Planck -> Point -> Microdot -> Millidot -> Dot
     */
    public static final Units Polkadots = new Units(
            new Unit("Planck", 0),
            new Unit("Point", 3),
            new Unit("Microdot", "uDOT", 6),
            new Unit("Millidot", "mDOT", 9),
            new Unit("Dot", "DOT", 12)
    );

    /**
     * Kusama testnet units
     *
     * Planck ->  Point -> MicroKSM -> MilliKSM -> KSM
     */
    public static final Units Kusamas = new Units(
            new Unit("Planck", 0),
            new Unit("Point", 3),
            new Unit("MicroKSM", "uKSM", 6),
            new Unit("MilliKSM", "mKSM", 9),
            new Unit("KSM", 12)
    );

    private static final Unit DOT_UNIT = Polkadots.getMain();
    private static final BigInteger DOT_MULTIPLIER = DOT_UNIT.getMultiplier();
    private static final BigDecimal DOT_MULTIPLIER_DECIMAL = new BigDecimal(DOT_MULTIPLIER);

    private BigInteger value;
    private Units units;

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

    public static DotAmount fromPlanks(long amount) {
        return new DotAmount(BigInteger.valueOf(amount), Polkadots);
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

    public Units getUnits() {
        return units;
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
