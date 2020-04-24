package io.emeraldpay.pjc.scale;

public enum CompactMode {

    SINGLE((byte)0b00),
    TWO((byte)0b01),
    FOUR((byte)0b10),
    BIGINT((byte)0b11);

    private byte value;

    private CompactMode(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static CompactMode byValue(byte value) {
        if (value == SINGLE.value) {
            return SINGLE;
        } else if (value == TWO.value) {
            return TWO;
        } else if (value == FOUR.value) {
            return FOUR;
        } else {
            return BIGINT;
        }
    }
}
