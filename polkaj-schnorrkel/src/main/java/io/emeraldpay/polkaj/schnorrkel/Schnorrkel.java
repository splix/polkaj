package io.emeraldpay.polkaj.schnorrkel;

/**
 * Schnorrkel implements Schnorr signature on Ristretto compressed Ed25519 points, as well as related protocols like
 * HDKD, MuSig, and a verifiable random function (VRF).
 *
 * The Java library is a wrapper around Rust implementation of the algorithms.
 *
 * @link <a href="https://github.com/w3f/schnorrkel">Rust implementatiion</a>
 * @link <a href="https://tools.ietf.org/html/rfc8032">RFC 8032 - Edwards-Curve Digital Signature Algorithm (EdDSA)</a>
 */
public class Schnorrkel {

    private static native byte[] sign(byte[] pubKey, byte[] secretKey, byte[] message);

    public static byte[] sign(byte[] message, Keypair keypair) throws SchnorrkelException {
        return sign(keypair.publicKey, keypair.secretKey, message);
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
