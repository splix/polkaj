package io.emeraldpay.polkaj.schnorrkel;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Schnorrkel implements Schnorr signature on Ristretto compressed Ed25519 points, as well as related protocols like
 * HDKD, MuSig, and a verifiable random function (VRF).
 * <br>
 * The Java library is a wrapper around Rust implementation of the algorithms.
 * <br>
 * See also:
 * <ul>
 *     <li><a href="https://github.com/w3f/schnorrkel">Rust implementatiion</a></li>
 *     <li><a href="https://tools.ietf.org/html/rfc8032">RFC 8032 - Edwards-Curve Digital Signature Algorithm (EdDSA)</a></li>
 * </ul>
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

    public static byte[] sign(byte[] message, KeyPair keypair) throws SchnorrkelException {
        return sign(keypair.getPublicKey(), keypair.getSecretKey(), message);
    }

    /**
     * Verify signature
     *
     * @param signature signature
     * @param message signed message
     * @param publicKey public key of the signer
     * @return true if signature is correct
     * @throws SchnorrkelException when signature or public key are invalid
     */
    public static boolean verify(byte[] signature, byte[] message, PublicKey publicKey) throws SchnorrkelException {
        return verify(signature, message, publicKey.getPublicKey());
    }

    public static KeyPair generateKeyPair() throws SchnorrkelException {
        try {
            return generateKeyPair(SecureRandom.getInstanceStrong());
        } catch (NoSuchAlgorithmException e) {
            throw new SchnorrkelException("Secure Random is not available");
        }
    }

    public static KeyPair generateKeyPair(SecureRandom random) throws SchnorrkelException {
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

        return new KeyPair(publicKey, secretKey);
    }

    static {
        System.loadLibrary("polkaj_schnorrkel");
    }

    private static native byte[] sign(byte[] publicKey, byte[] secretKey, byte[] message);
    private static native byte[] keypairFromSeed(byte[] seed);
    private static native boolean verify(byte[] signature, byte[] message, byte[] publicKey) throws SchnorrkelException;

    /**
     * Public Key
     */
    public static class PublicKey {

        private final byte[] publicKey;

        public PublicKey(byte[] publicKey) {
            this.publicKey = publicKey;
        }
        public byte[] getPublicKey() {
            return publicKey;
        }
    }

    /**
     * Pair of Public and Secret Keys
     */
    public static class KeyPair extends PublicKey {

        private final byte[] secretKey;

        public KeyPair(PublicKey publicKey, byte[] secretKey) {
            this(publicKey.publicKey, secretKey);
        }

        public KeyPair(byte[] publicKey, byte[] secretKey) {
            super(publicKey);
            this.secretKey = secretKey;
        }

        public byte[] getSecretKey() {
            return secretKey;
        }
    }

}
