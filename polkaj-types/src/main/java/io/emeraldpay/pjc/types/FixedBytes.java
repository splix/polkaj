package io.emeraldpay.pjc.types;

import java.util.Arrays;

abstract public class FixedBytes {
    protected final byte[] value;

    protected FixedBytes(byte[] value, int expectedSize) {
        if (value == null) {
            throw new NullPointerException("Value is null");
        }
        if (value.length != expectedSize) {
            throw new IllegalArgumentException("Value size must be " + expectedSize + "; received: " + value.length);
        }
        this.value = value.clone();
    }

    protected static byte[] parseHex(String hex, int expectedSize) {
        if (hex.length() == expectedSize * 2 + 2 && hex.startsWith("0x")) {
            hex = hex.substring(2);
        }
        if (hex.length() != expectedSize * 2) {
            throw new IllegalArgumentException("Invalid hex size: " + hex.length());
        }
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
        char[] hex = new char[value.length * 2];
        for (int i = 0; i < value.length; i++) {
            byte b = value[i];
            hex[i * 2] = Character.forDigit((b & 0xf0) >> 4, 16);
            hex[i * 2 + 1] = Character.forDigit(b & 0x0f, 16);
        }
        return new String(hex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FixedBytes)) return false;
        FixedBytes fixedBytes = (FixedBytes) o;
        return Arrays.equals(value, fixedBytes.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    protected int compareTo(FixedBytes o) {
        if (value.length != o.value.length) {
            throw new IllegalStateException("Different size " + value.length + " != " + o.value.length);
        }
        for (int i = 0; i < value.length; i++) {
            if (value[i] != o.value[i]) {
                return value[i] - o.value[i];
            }
        }
        return 0;
    }
}
