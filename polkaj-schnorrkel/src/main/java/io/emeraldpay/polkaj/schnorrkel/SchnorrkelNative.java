package io.emeraldpay.polkaj.schnorrkel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
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
        try {
            return generateKeyPair(SecureRandom.getInstanceStrong());
        } catch (NoSuchAlgorithmException e) {
            throw new SchnorrkelException("Secure Random is not available");
        }
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
        // But since it may be already processed and cached we need to update the current value
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(ClassLoader.class, MethodHandles.lookup());
            VarHandle usrPathsField = lookup.findStaticVarHandle(ClassLoader.class,
                    "usr_paths", String[].class);
            MethodHandle initializePathMethod = lookup.findStatic(ClassLoader.class,
                    "initializePath", MethodType.methodType(String[].class, String.class));
            usrPathsField.set(initializePathMethod.invoke(libraryPathProperty));
        } catch (Throwable e) {
            System.err.println("Unable to update usr_paths field. " + e.getClass() + ":" + e.getMessage());
        }
    }

}
