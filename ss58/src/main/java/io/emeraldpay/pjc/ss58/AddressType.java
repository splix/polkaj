package io.emeraldpay.pjc.ss58;

/**
 * @link https://github.com/paritytech/substrate/wiki/External-Address-Format-(SS58)
 */
public abstract class AddressType {

    private byte value;

    private AddressType(byte value) {
        this.value = value;
    }
    private AddressType(int value) {
        //values starting from 64 are reserved by the spec at this moment
        if (value < 0 || value >= 64) {
            throw new IllegalArgumentException("Unsupported value: " + value);
        }
        this.value = (byte)value;
    }

    public byte getValue() {
        return value;
    }

    static class Network extends AddressType {

        static Network LIVE = new Network(0b00000000);
        static Network LIVE_SECONDARY = new Network(0b00000001);
        static Network CANARY = new Network(0b00000010);
        static Network CANARY_SECONDARY = new Network(0b00000011);
        static Network EDGEWARE_BERLIN = new Network(0b00000111);
        static Network KULUPU = new Network(0b00010000);
        static Network KULUPU_SECONDARY = new Network(0b00010001);
        static Network DOTHEREUM = new Network(0b00010100);
        static Network SUBSTRATE = new Network(0b00101010);
        static Network SUBSTRATE_SECONDARY = new Network(0b00101011);

        private Network(int value) {
            super(value);
        }
    }

    static class Key extends AddressType {
        private Key(int value) {
            super(value);
        }

        static Key SR25519 = new Key(0b00110000);
        static Key ED25519 = new Key(0b00110001);
        static Key SECP256K1 = new Key(0b00110010);
    }

    static class Custom extends AddressType {
        public Custom(byte value) {
            super(value);
        }

        public Custom(int value) {
            super(value);
        }
    }

}
