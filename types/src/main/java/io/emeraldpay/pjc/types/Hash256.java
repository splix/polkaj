package io.emeraldpay.pjc.types;

import java.util.Arrays;

/**
 * A 256 bit value, commonly used as a hash
 */
public class Hash256 implements Comparable<Hash256> {

    /**
     * Length in bytes (32 byte)
     */
    public static final int SIZE_BYTES = 32;
    /**
     * Length for a hex string representing value (64 characters)
     */
    public static final int SIZE_HEX = SIZE_BYTES * 2;
    /**
     * Length for a hex string with 0x prefix (66 characters)
     */
    public static final int SIZE_HEX_FORMATTER = SIZE_HEX + 2;

    private byte[] value;

    /**
     * Create a new value. Makes sure the input is correct, if not throws an exception
     *
     * @param value 32 byte value
     * @throws NullPointerException if value is null
     * @throws IllegalArgumentException is size is not 32 bytes
     */
    public Hash256(byte[] value) {
        if (value == null) {
            throw new NullPointerException("Hash value is null");
        }
        if (value.length != SIZE_BYTES) {
            throw new IllegalArgumentException("Hash size must be " + SIZE_BYTES + "; received: " + value.length);
        }
        this.value = value.clone();
    }

    /**
     * Creates an empty zeroed instance
     *
     * @return empty hash
     */
    public static Hash256 empty() {
        return new Hash256(new byte[SIZE_BYTES]);
    }

    /**
     * Parse hex value and create a new instance
     *
     * @param hex hex value, may optionally start with 0x prefix
     * @return hash instance
     * @throws IllegalArgumentException if value has invalid length
     * @throws NumberFormatException if value has invalid format (non-hex characters, etc)
     */
    public static Hash256 from(String hex) {
        if (hex.length() == SIZE_HEX_FORMATTER && hex.startsWith("0x")) {
            hex = hex.substring(2);
        }
        if (hex.length() != SIZE_HEX) {
            throw new IllegalArgumentException("Invalid hex size: " + hex.length());
        }
        byte[] parsed = parseHex(hex);
        return new Hash256(parsed);
    }

    private static byte[] parseHex(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = Integer.valueOf(hex.substring(i, i+2), 16).byteValue();
        }
        return data;
    }

    /**
     * @return bytes value
     */
    public byte[] getBytes() {
        //make a copy to ensure immutability
        return value.clone();
    }

    public String toString() {
        char[] hex = new char[SIZE_HEX];
        for (int i = 0; i < SIZE_BYTES; i++) {
            byte b = value[i];
            hex[i * 2] = Character.forDigit((b & 0xf0) >> 4, 16);
            hex[i * 2 + 1] = Character.forDigit(b & 0x0f, 16);
        }
        return new String(hex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hash256)) return false;
        Hash256 hash256 = (Hash256) o;
        return Arrays.equals(value, hash256.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public int compareTo(Hash256 o) {
        for (int i = 0; i < SIZE_BYTES; i++) {
            if (value[i] != o.value[i]) {
                return value[i] - o.value[i];
            }
        }
        return 0;
    }
}
