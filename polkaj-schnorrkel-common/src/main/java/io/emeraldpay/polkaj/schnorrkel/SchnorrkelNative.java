package io.emeraldpay.polkaj.schnorrkel;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Wrapper around Rust implementation of the algorithms.
 * <br>
 * See also:
 * <ul>
 *     <li><a href="https://github.com/w3f/schnorrkel">Rust implementatiion</a></li>
 *     <li><a href="https://tools.ietf.org/html/rfc8032">RFC 8032 - Edwards-Curve Digital Signature Algorithm (EdDSA)</a></li>
 * </ul>
 */
public class SchnorrkelNative extends Schnorrkel {

    @Override
    public byte[] sign(byte[] message, KeyPair keypair) throws SchnorrkelException {
        return SchnorrkelNative.sign(keypair.getPublicKey(), keypair.getSecretKey(), message);
    }

    @Override
    public boolean verify(byte[] signature, byte[] message, PublicKey publicKey) throws SchnorrkelException {
        return SchnorrkelNative.verify(signature, message, publicKey.getPublicKey());
    }

    @Override
    public KeyPair generateKeyPair() throws SchnorrkelException {
        SecureRandom secureRandom;
        try{
            try{
                SecureRandom.class.getMethod("getInstanceStrong");
                //noinspection NewApi
                secureRandom = SecureRandom.getInstanceStrong();
            }catch (NoSuchMethodException e){
                secureRandom = new SecureRandom(); //Android 24 & 25 do not have getInstanceStrong
            }
        }catch(NoSuchAlgorithmException e){
            throw new SchnorrkelException("Secure Random is not available");
        }
        return generateKeyPair(secureRandom);
    }

    @Override
    public KeyPair generateKeyPair(SecureRandom random) throws SchnorrkelException {
        byte[] seed = new byte[32];
        random.nextBytes(seed);
        byte[] key = keypairFromSeed(seed);
        return decodeKeyPair(key);
    }

    @Override
    public KeyPair generateKeyPairFromSeed(byte[] seed) throws SchnorrkelException {
        byte[] key = keypairFromSeed(seed);
        return decodeKeyPair(key);
    }

    @Override
    public KeyPair deriveKeyPair(KeyPair base, ChainCode chainCode) throws SchnorrkelException {
        byte[] key = deriveHard(encodeKeyPair(base), chainCode.getValue());
        return decodeKeyPair(key);
    }

    @Override
    public KeyPair deriveKeyPairSoft(KeyPair base, ChainCode chainCode) throws SchnorrkelException {
        byte[] key = deriveSoft(encodeKeyPair(base), chainCode.getValue());
        return decodeKeyPair(key);
    }

    @Override
    public PublicKey derivePublicKeySoft(PublicKey base, ChainCode chainCode) throws SchnorrkelException {
        byte[] key = derivePublicKeySoft(base.getPublicKey(), chainCode.getValue());
        return new Schnorrkel.PublicKey(key);
    }

    private static Schnorrkel.KeyPair decodeKeyPair(byte[] key) throws SchnorrkelException {
        if (key.length != KEYPAIR_LENGTH) {
            throw new SchnorrkelException("Invalid key generated");
        }
        byte[] secretKey = new byte[SECRET_KEY_LENGTH];
        System.arraycopy(key, 0, secretKey, 0, SECRET_KEY_LENGTH);
        byte[] publicKey = new byte[PUBLIC_KEY_LENGTH];
        System.arraycopy(key, SECRET_KEY_LENGTH, publicKey, 0, PUBLIC_KEY_LENGTH);

        return new Schnorrkel.KeyPair(publicKey, secretKey);
    }

    private static byte[] encodeKeyPair(Schnorrkel.KeyPair keyPair) {
        byte[] result = new byte[KEYPAIR_LENGTH];
        System.arraycopy(keyPair.getSecretKey(), 0, result, 0, keyPair.getSecretKey().length);
        System.arraycopy(keyPair.getPublicKey(), 0, result, SECRET_KEY_LENGTH, keyPair.getPublicKey().length);
        return result;
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
            // files for current OS into a temp dir then load the file.
            if(!extractAndLoadJNI()) {
                // load the native library, this is for running tests and android
                System.loadLibrary(LIBNAME);
            }
        } catch (IOException e) {
            System.err.println("Failed to extract JNI library from Jar file. " + e.getClass() + ":" + e.getMessage());
        } catch (UnsatisfiedLinkError e){
            System.err.println("Failed to load native library. Polkaj Schnorrkel methods are unavailable. Error: " + e.getMessage());
        }
    }

    ////noinspection NewApi added to allow android lint check to succeed for api 24-25
    // these apis will not be called in that case
    private static boolean extractAndLoadJNI() throws IOException {
        // define which of files bundled with Jar to extract
        if(System.getProperty("java.runtime.name", "unknown").contains("android")) return false;
        String os = System.getProperty("os.name", "unknown").toLowerCase();
        if (os.contains("win")) {
            os = "windows";
        } else if (os.contains("mac")) {
            os = "macos";
        } else if (os.contains("nux")) {
            os = "linux";
        } else {
            System.err.println("Unknown OS: " + os + ". Unable to setup native library for Polkaj Schnorrkel");
            return false;
        }
        String filename = System.mapLibraryName(LIBNAME);
        String classpathFile = "/native/" + os + "/" + filename;

        // extract native lib to the filesystem
        InputStream lib = Schnorrkel.class.getResourceAsStream(classpathFile);
        System.out.println(classpathFile);
        if (lib == null) {
            System.err.println("Library " + classpathFile + " is not found in the classpath");
            return false;
        }
        //noinspection NewApi
        Path dir = Files.createTempDirectory(LIBNAME);
        //noinspection NewApi
        Path target = dir.resolve(filename);

        //noinspection NewApi
        Files.copy(lib, target);
        //noinspection NewApi
        System.load(target.toFile().getAbsolutePath());
        System.out.println("library " + classpathFile + " is loaded");

        // setup JVM to delete files on exit, when possible
        //noinspection NewApi
        target.toFile().deleteOnExit();
        //noinspection NewApi
        dir.toFile().deleteOnExit();
        return true;
    }

}
