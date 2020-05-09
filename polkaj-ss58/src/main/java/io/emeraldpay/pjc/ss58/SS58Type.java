package io.emeraldpay.pjc.ss58;

/**
 * @see <a href="https://github.com/paritytech/substrate/wiki/External-Address-Format-(SS58)">https://github.com/paritytech/substrate/wiki/External-Address-Format-(SS58)</a>
 */
public abstract class SS58Type {

    private byte value;

    private SS58Type(byte value) {
        this.value = value;
    }
    private SS58Type(int value) {
        //values starting from 64 are reserved by the spec at this moment
        if (value < 0 || value >= 64) {
            throw new IllegalArgumentException("Unsupported value: " + value);
        }
        this.value = (byte)value;
    }

    public byte getValue() {
        return value;
    }

    public static class Network extends SS58Type {

        public static Network LIVE = new Network(0b00000000);
        public static Network LIVE_SECONDARY = new Network(0b00000001);
        public static Network CANARY = new Network(0b00000010);
        public static Network CANARY_SECONDARY = new Network(0b00000011);
        public static Network EDGEWARE_BERLIN = new Network(0b00000111);
        public static Network KULUPU = new Network(0b00010000);
        public static Network KULUPU_SECONDARY = new Network(0b00010001);
        public static Network DOTHEREUM = new Network(0b00010100);
        public static Network SUBSTRATE = new Network(0b00101010);
        public static Network SUBSTRATE_SECONDARY = new Network(0b00101011);

        private static Network[] ALL = {
                LIVE, LIVE_SECONDARY,
                CANARY, CANARY_SECONDARY,
                EDGEWARE_BERLIN,
                KULUPU, KULUPU_SECONDARY,
                DOTHEREUM,
                SUBSTRATE, SUBSTRATE_SECONDARY
        };

        private Network(int value) {
            super(value);
        }

        public static Network from(byte value) {
            for (Network n: ALL) {
                if (n.getValue() == value) {
                    return n;
                }
            }
            throw new IllegalArgumentException("Unsupported network: " + value);
        }
    }

    public static class Key extends SS58Type {
        private Key(int value) {
            super(value);
        }

        public static Key SR25519 = new Key(0b00110000);
        public static Key ED25519 = new Key(0b00110001);
        public static Key SECP256K1 = new Key(0b00110010);
    }

    public static class Custom extends SS58Type {
        public Custom(byte value) {
            super(value);
        }

        public Custom(int value) {
            super(value);
        }
    }

}
