package io.emeraldpay.polkaj.types;

/**
 * A 256 bit value, commonly used as a hash
 */
public class Hash256 extends FixedBytes implements Comparable<Hash256> {

    /**
     * Length in bytes (32 byte)
     */
    public static final int SIZE_BYTES = 32;

    /**
     * Create a new value. Makes sure the input is correct, if not throws an exception
     *
     * @param value 32 byte value
     * @throws NullPointerException if value is null
     * @throws IllegalArgumentException is size is not 32 bytes
     */
    public Hash256(byte[] value) {
        super(value, SIZE_BYTES);
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
        byte[] parsed = parseHex(hex, SIZE_BYTES);
        return new Hash256(parsed);
    }

    @Override
    public int compareTo(Hash256 o) {
        return super.compareTo(o);
    }
}
