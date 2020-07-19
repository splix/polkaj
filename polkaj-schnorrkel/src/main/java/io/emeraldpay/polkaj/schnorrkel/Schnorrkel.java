package io.emeraldpay.polkaj.schnorrkel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

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

    /**
     * Length in bytes of chain codes
     */
    public static final int CHAIN_CODE_LENGTH = 32;

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
        return decodeKeyPair(key);
    }

    public static KeyPair generateKeyPairFromSeed(byte[] seed) throws SchnorrkelException {
        byte[] key = keypairFromSeed(seed);
        return decodeKeyPair(key);
    }

    public static KeyPair deriveKeyPair(KeyPair base, ChainCode chainCode) throws SchnorrkelException {
        byte[] key = deriveHard(encodeKeyPair(base), chainCode.getValue());
        return decodeKeyPair(key);
    }

    public static KeyPair deriveKeyPairSoft(KeyPair base, ChainCode chainCode) throws SchnorrkelException {
        byte[] key = deriveSoft(encodeKeyPair(base), chainCode.getValue());
        return decodeKeyPair(key);
    }

    public static PublicKey derivePublicKeySoft(PublicKey base, ChainCode chainCode) throws SchnorrkelException {
        byte[] key = derivePublicKeySoft(base.publicKey, chainCode.getValue());
        return new PublicKey(key);
    }

    private static KeyPair decodeKeyPair(byte[] key) throws SchnorrkelException {
        if (key.length != KEYPAIR_LENGTH) {
            throw new SchnorrkelException("Invalid key generated");
        }
        byte[] secretKey = new byte[SECRET_KEY_LENGTH];
        System.arraycopy(key, 0, secretKey, 0, SECRET_KEY_LENGTH);
        byte[] publicKey = new byte[PUBLIC_KEY_LENGTH];
        System.arraycopy(key, SECRET_KEY_LENGTH, publicKey, 0, PUBLIC_KEY_LENGTH);

        return new KeyPair(publicKey, secretKey);
    }

    private static byte[] encodeKeyPair(KeyPair keyPair) {
        byte[] result = new byte[KEYPAIR_LENGTH];
        System.arraycopy(keyPair.secretKey, 0, result, 0, keyPair.secretKey.length);
        System.arraycopy(keyPair.getPublicKey(), 0, result, SECRET_KEY_LENGTH, keyPair.getPublicKey().length);
        return result;
    }

    // ====================== Supporting Classes ======================

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PublicKey)) return false;
            if (o instanceof KeyPair) return false;
            PublicKey publicKey1 = (PublicKey) o;
            return Arrays.equals(publicKey, publicKey1.publicKey);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(publicKey);
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof KeyPair)) return false;
            KeyPair keyPair = (KeyPair) o;
            return Arrays.equals(getPublicKey(), keyPair.getPublicKey())
                    && Arrays.equals(secretKey, keyPair.secretKey);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + Arrays.hashCode(secretKey);
            return result;
        }
    }

    public static class ChainCode {
        private final byte[] value;

        public ChainCode(byte[] value) {
            if (value.length != CHAIN_CODE_LENGTH) {
                throw new IllegalArgumentException("Chain code must be " + CHAIN_CODE_LENGTH + " bytes");
            }
            this.value = value;
        }

        public static ChainCode from(byte[] value) {
            if (value.length > CHAIN_CODE_LENGTH) {
                throw new IllegalArgumentException("Chain code must be " + CHAIN_CODE_LENGTH + " bytes (if less, then padded with zeroes)");
            }
            if (value.length == CHAIN_CODE_LENGTH) {
                return new ChainCode(value);
            }
            byte[] full = new byte[CHAIN_CODE_LENGTH];
            System.arraycopy(value, 0, full, 0, value.length);
            return new ChainCode(full);
        }

        public byte[] getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ChainCode)) return false;
            ChainCode chainCode = (ChainCode) o;
            return Arrays.equals(value, chainCode.value);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(value);
        }
    }

    // ====================== Mapping to the Native Library ======================

    private static native byte[] sign(byte[] publicKey, byte[] secretKey, byte[] message);
    private static native byte[] keypairFromSeed(byte[] seed);
    private static native boolean verify(byte[] signature, byte[] message, byte[] publicKey);
    private static native byte[] deriveHard(byte[] secret, byte[] cc);
    private static native byte[] deriveSoft(byte[] secret, byte[] cc);
    private static native byte[] derivePublicKeySoft(byte[] publicKey, byte[] cc);

    // ====================== LOAD NATIVE LIBRARY ======================

    private static final String LIBNAME = "polkaj_schnorrkel";

    static {
        try {
            // JVM needs native libraries to be loaded from filesystem, so first we need to extract
            // files for current OS into a temp dir
            extractJNI();
        } catch (IOException e) {
            System.err.println("Failed to extract JNI library from Jar file. " + e.getClass() + ":" + e.getMessage());
        }
        try {
            // load the native library
            System.loadLibrary(LIBNAME);
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Failed to load native library. Polkaj Schnorrkel methods are unavailable. Error: " + e.getMessage());
        }
    }

    private static void extractJNI() throws IOException {
        // define which of files bundled with Jar to extract
        String os = System.getProperty("os.name", "unknown").toLowerCase();
        if (os.contains("win")) {
            os = "windows";
        } else if (os.contains("mac")) {
            os = "macos";
        } else if (os.contains("nux")) {
            os = "linux";
        } else {
            System.err.println("Unknown OS: " + os + ". Unable to setup native library for Polkaj Schnorrkel");
            return;
        }
        String filename = System.mapLibraryName(LIBNAME);
        String classpathFile = "/native/" + os + "/" + filename;

        // extract native lib to the filesystem
        InputStream lib = Schnorrkel.class.getResourceAsStream(classpathFile);
        if (lib == null) {
            System.err.println("Library " + classpathFile + " is not found in the classpath");
            return;
        }
        Path dir = Files.createTempDirectory(LIBNAME);
        Path target = dir.resolve(filename);
        Files.copy(lib, target);

        // setup JVM to delete files on exit, when possible
        target.toFile().deleteOnExit();
        dir.toFile().deleteOnExit();

        // prepare new path to native libraries, including the directly with just extracted file
        final String libraryPathProperty = "java.library.path";
        String userLibs = System.getProperty(libraryPathProperty);
        if (userLibs == null || "".equals(userLibs)) {
            userLibs = dir.toAbsolutePath().toString();
        } else {
            userLibs = userLibs + File.pathSeparatorChar + dir.toAbsolutePath().toString();
        }

        // Update paths to search for native libraries
        System.setProperty(libraryPathProperty, userLibs);
        // But since it may be already processed and cached we need to erase the current value
        try {
            final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
            sysPathsField.setAccessible(true);
            sysPathsField.set(null, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Unable to update sys_paths field. " + e.getClass() + ":" + e.getMessage());
        }
    }
}
