package io.emeraldpay.pjc.ss58;

public class SS58 {

    private final SS58Type type;
    private final byte[] value;
    private final byte[] checksum;

    public SS58(SS58Type type, byte[] value, byte[] checksum) {
        this.type = type;
        this.value = value;
        this.checksum = checksum;
    }

    public SS58Type getType() {
        return type;
    }

    public byte[] getValue() {
        return value;
    }

    public byte[] getChecksum() {
        return checksum;
    }
}
