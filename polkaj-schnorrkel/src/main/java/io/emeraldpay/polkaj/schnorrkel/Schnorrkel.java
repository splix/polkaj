package io.emeraldpay.polkaj.schnorrkel;

public class Schnorrkel {

    private static native String sign(byte[] message, byte[] sk);

    public static String sign(byte[] message, Keypair keypair) {
        return sign(message, keypair.secretKey);
    }

    static {
        System.loadLibrary("polkaj_schnorrkel");
    }

    static class Keypair {
        private final byte[] publicKey;
        private final byte[] secretKey;

        public Keypair(byte[] publicKey, byte[] secretKey) {
            this.publicKey = publicKey;
            this.secretKey = secretKey;
        }

        public byte[] getPublicKey() {
            return publicKey;
        }

        public byte[] getSecretKey() {
            return secretKey;
        }
    }
}
