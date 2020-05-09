package io.emeraldpay.pjc.types;

/**
 * A 512 bit value, commonly used as a hash
 */
public class Hash512 extends FixedBytes implements Comparable<Hash512> {

    /**
     * Length in bytes (64 byte)
     */
    public static final int SIZE_BYTES = 64;

    /**
     * Create a new value. Makes sure the input is correct, if not throws an exception
     *
     * @param value 64 byte value
     * @throws NullPointerException if value is null
     * @throws IllegalArgumentException is size is not 64 bytes
     */
    public Hash512(byte[] value) {
        super(value, SIZE_BYTES);
    }

    /**
     * Creates an empty zeroed instance
     *
     * @return empty hash
     */
    public static Hash512 empty() {
        return new Hash512(new byte[SIZE_BYTES]);
    }

    /**
     * Parse hex value and create a new instance
     *
     * @param hex hex value, may optionally start with 0x prefix
     * @return hash instance
     * @throws IllegalArgumentException if value has invalid length
     * @throws NumberFormatException if value has invalid format (non-hex characters, etc)
     */
    public static Hash512 from(String hex) {
        byte[] parsed = parseHex(hex, SIZE_BYTES);
        return new Hash512(parsed);
    }

    @Override
    public int compareTo(Hash512 o) {
        return super.compareTo(o);
    }
}
