package io.emeraldpay.polkaj.types;

import java.util.Arrays;

public class ByteData {

    protected final byte[] value;

    public ByteData(byte[] value) {
        if (value == null) {
            throw new NullPointerException("Value is null");
        }
        this.value = value.clone();
    }

    public static ByteData from(String hex) {
        byte[] value = FixedBytes.parseHex(hex);
        return new ByteData(value);
    }

    public static ByteData empty() {
        return new ByteData(new byte[0]);
    }

    /**
     * @return bytes value
     */
    public byte[] getBytes() {
        //make a copy to ensure immutability
        return value.clone();
    }

    public String toString() {
        char[] hex = new char[value.length * 2 + 2];
        hex[0] = '0';
        hex[1] = 'x';
        for (int i = 0; i < value.length; i++) {
            byte b = value[i];
            hex[2 + i * 2] = Character.forDigit((b & 0xf0) >> 4, 16);
            hex[2 + i * 2 + 1] = Character.forDigit(b & 0x0f, 16);
        }
        return new String(hex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ByteData)) return false;
        ByteData byteData = (ByteData) o;
        return Arrays.equals(value, byteData.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }
}
