package io.emeraldpay.pjc.types;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

/**
 * List of different units for a DOT amount, each has own name and different amount of decimals
 *
 * @see DotAmount
 * @see DotAmount#Polkadots
 * @see DotAmount#Kusamas
 * @see Units.Unit
 */
public class Units {
    private Unit[] units;

    /**
     * Create a new list of units, the provided list must be sorted by decimals, from smallest to largest
     *
     * @param units units
     */
    public Units(Unit... units) {
        if (units == null) {
            throw new NullPointerException("Units are not provided");
        }
        if (units.length == 0) {
            throw new IllegalArgumentException("Should have at least one unit");
        }
        for (int i = 1; i < units.length; i++) {
            if (units[i].decimals <= units[i-1].decimals) {
                throw new IllegalArgumentException("Units are not ordered by decimal value");
            }
        }
        this.units = units;
    }

    public Unit[] getUnits() {
        return units;
    }

    public Unit getMain() {
        return units[units.length - 1];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Units)) return false;
        Units units1 = (Units) o;
        return Arrays.equals(units, units1.units);
    }

    @Override
    public int hashCode() {
        return getMain().hashCode();
    }

    public String toString() {
        return getMain().toString();
    }

    /**
     * A single unit, with it's own name and decimals. May have a short name, if exists (i.e., Microdot is the full name, and uDOT is the short name)
     */
    public static class Unit {
        private int decimals;
        private String name;
        private String shortName;

        public Unit(String name, String shortName, int decimals) {
            this.decimals = decimals;
            this.shortName = shortName;
            this.name = name;
        }


        public Unit(String name, int decimals) {
            this(name, name, decimals);
        }

        public int getDecimals() {
            return decimals;
        }

        public String getName() {
            return name;
        }

        public String getShortName() {
            return shortName;
        }

        public BigInteger getMultiplier() {
            return BigInteger.TEN.pow(decimals);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Unit)) return false;
            Unit unit = (Unit) o;
            return decimals == unit.decimals &&
                    name.equals(unit.name) &&
                    Objects.equals(shortName, unit.shortName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(decimals, name);
        }

        public String toString() {
            return name;
        }
    }
}
