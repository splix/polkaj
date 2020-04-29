package io.emeraldpay.pjc.ss58;

public class SS58 {

    private byte type;
    private byte[] value;
    private byte[] checksum;

    public SS58(byte type, byte[] value, byte[] checksum) {
        this.type = type;
        this.value = value;
        this.checksum = checksum;
    }

    public byte getType() {
        return type;
    }

    public byte[] getValue() {
        return value;
    }

    public byte[] getChecksum() {
        return checksum;
    }
}
