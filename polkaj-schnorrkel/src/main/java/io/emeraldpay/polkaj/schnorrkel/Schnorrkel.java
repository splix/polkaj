package io.emeraldpay.polkaj.schnorrkel;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

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

    /**
     * The length of the "key" portion of a Ristretto Schnorr secret key, in bytes.
     */
    public static final int SECRET_KEY_MAIN_LENGTH = 32;

    /**
     * The length of the "nonce" portion of a Ristretto Schnorr secret key, in bytes.
     */
    public static final int SECRET_KEY_NONCE_LENGTH = 32;

    /**
     * The length of a Ristretto Schnorr `PublicKey`, in bytes.
     */
    public static final int PUBLIC_KEY_LENGTH = 32;

    /**
     * The length of a Ristretto Schnorr key, `SecretKey`, in bytes.
     */
    public static final int SECRET_KEY_LENGTH = SECRET_KEY_MAIN_LENGTH + SECRET_KEY_NONCE_LENGTH;

    /**
     * The length of an Ristretto Schnorr `Keypair`, in bytes.
     */
    public static final int KEYPAIR_LENGTH = SECRET_KEY_LENGTH + PUBLIC_KEY_LENGTH;

    public static byte[] sign(byte[] message, Keypair keypair) throws SchnorrkelException {
        return sign(keypair.publicKey, keypair.secretKey, message);
    }

    public static native boolean verify(byte[] signature, byte[] message, byte[] publicKey) throws SchnorrkelException;

    public static Keypair generateKey() throws SchnorrkelException {
        try {
            return generateKey(SecureRandom.getInstanceStrong());
        } catch (NoSuchAlgorithmException e) {
            throw new SchnorrkelException("Secure Random is not available");
        }
    }

    public static Keypair generateKey(SecureRandom random) throws SchnorrkelException {
        byte[] seed = new byte[32];
        random.nextBytes(seed);
        byte[] key = keypairFromSeed(seed);

        if (key.length != KEYPAIR_LENGTH) {
            throw new SchnorrkelException("Invalid key generated");
        }
        byte[] secretKey = new byte[SECRET_KEY_LENGTH];
        System.arraycopy(key, 0, secretKey, 0, SECRET_KEY_LENGTH);
        byte[] publicKey = new byte[PUBLIC_KEY_LENGTH];
        System.arraycopy(key, SECRET_KEY_LENGTH, publicKey, 0, PUBLIC_KEY_LENGTH);

        return new Keypair(publicKey, secretKey);
    }

    static {
        System.loadLibrary("polkaj_schnorrkel");
    }

    private static native byte[] sign(byte[] publicKey, byte[] secretKey, byte[] message);

    private static native byte[] keypairFromSeed(byte[] seed);

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
