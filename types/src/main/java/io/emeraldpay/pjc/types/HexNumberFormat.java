package io.emeraldpay.pjc.types;

import java.math.BigInteger;

/**
 * Formatting and parsing utilities for numbers represented in hex with 0x prefix
 */
public class HexNumberFormat {

    /**
     * Clean and verify hex formatted number
     *
     * "0x1f" -> "1f"
     *
     * @param hex a hex formatted number with 0x prefix
     * @return hex string convenient for standard hex parsing
     */
    public static String clean(String hex) {
        if (hex == null) {
            return null;
        }
        if (!hex.startsWith("0x")) {
            throw new IllegalArgumentException("No hex prefix");
        }
        hex = hex.substring(2);
        if (hex.length() == 0) {
            return null;
        }
        //make sure it doesn't start with any special symbol or sign
        if (!(Character.isDigit(hex.charAt(0)) || Character.isAlphabetic(hex.charAt(0)))) {
            throw new IllegalArgumentException("Invalid hex");
        }
        return hex;
    }

    public static Long parseLong(String hex) {
        hex = clean(hex);
        if (hex == null) {
            return null;
        }
        return Long.parseLong(hex, 16);
    }

    public static Integer parseInt(String hex) {
        hex = clean(hex);
        if (hex == null) {
            return null;
        }
        return Integer.parseInt(hex, 16);
    }

    public static BigInteger parseBigInt(String hex) {
        hex = clean(hex);
        if (hex == null) {
            return null;
        }
        return new BigInteger(hex, 16);
    }

    public static String toString(Long val) {
        if (val == null) {
            return "0x";
        }
        if (val < 0) {
            throw new IllegalArgumentException("Negative number: " + val);
        }
        return "0x" + Long.toString(val, 16);
    }

    public static String toString(Integer val) {
        if (val == null) {
            return "0x";
        }
        if (val < 0) {
            throw new IllegalArgumentException("Negative number: " + val);
        }
        return "0x" + Integer.toString(val, 16);
    }

    public static String toString(BigInteger val) {
        if (val == null) {
            return "0x";
        }
        if (val.signum() < 0) {
            throw new IllegalArgumentException("Negative number: " + val);
        }
        return "0x" + val.toString(16);
    }

}
