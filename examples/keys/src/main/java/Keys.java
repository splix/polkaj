import io.emeraldpay.polkaj.schnorrkel.Schnorrkel;
import io.emeraldpay.polkaj.schnorrkel.SchnorrkelException;
import io.emeraldpay.polkaj.ss58.SS58Type;
import io.emeraldpay.polkaj.types.Address;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class Keys {

    public static void createNewKey() throws SchnorrkelException {
        System.out.println("Generate a new Root Key + derive a `demo` address from that key");
        System.out.println("");
        Schnorrkel.KeyPair rootKey = Schnorrkel.getInstance().generateKeyPair();
        System.out.println("  Root Key: " + Hex.encodeHexString(rootKey.getSecretKey()));
        System.out.println("");
        Schnorrkel.KeyPair key = Schnorrkel.getInstance().deriveKeyPair(rootKey, Schnorrkel.ChainCode.from("demo".getBytes()));

        Address address = new Address(SS58Type.Network.CANARY, key.getPublicKey());
        System.out.println("   Address: " + address);
        System.out.println("Public Key: " + Hex.encodeHexString(key.getPublicKey()));
        System.out.println("Secret Key: " + Hex.encodeHexString(key.getSecretKey()));
    }

    public static void derive() throws SchnorrkelException {
        System.out.println("Derive new Address from existing");
        System.out.println("");
        Address address = Address.from("HhjZogQpUMuQEu8ChSAQUWj9UCnqAmCkogitYjqzCqD7xrq");
        System.out.println("  Address (curr): " + address);
        Schnorrkel.PublicKey publicKey = new Schnorrkel.PublicKey(address.getPubkey());

        Schnorrkel.PublicKey derived = Schnorrkel.getInstance().derivePublicKeySoft(publicKey, Schnorrkel.ChainCode.from("demo".getBytes()));
        Address anotherAddress = new Address(SS58Type.Network.CANARY, derived.getPublicKey());
        System.out.println("   Address (new): " + anotherAddress);
        System.out.println("Public Key (new): " + Hex.encodeHexString(derived.getPublicKey()));
    }

    public static void sign() throws DecoderException, SchnorrkelException {
        Schnorrkel.KeyPair aliceKey = Schnorrkel.getInstance().generateKeyPairFromSeed(
                Hex.decodeHex("e5be9a5092b81bca64be81d212e7f2f9eba183bb7a90954f7b76361f6edb5c0a")
        );
        byte[] message = Hex.decodeHex(
                "8a3476995d076964c8c8517c8a1a504da91dc2b16ab36fb04ca22734e572619be108ee699592ccb9b1344835352e42c9"
        );
        byte[] signature = Schnorrkel.getInstance().sign(message, aliceKey);
        System.out.println("Signature: " + Hex.encodeHexString(signature));
    }

    public static void verifyWithPubkey() throws DecoderException, SchnorrkelException {
        Schnorrkel.KeyPair aliceKey = Schnorrkel.getInstance().generateKeyPairFromSeed(
                Hex.decodeHex("e5be9a5092b81bca64be81d212e7f2f9eba183bb7a90954f7b76361f6edb5c0a")
        );
        byte[] message = Hex.decodeHex(
                "8a3476995d076964c8c8517c8a1a504da91dc2b16ab36fb04ca22734e572619be108ee699592ccb9b1344835352e42c9"
        );
        byte[] signature = Hex.decodeHex("e2525d278d3d4b32ca3372b6d2c32c1405f641a5c2309a94da416c32359ac76e485c6baa69baa66def1c3a46c76fc38fb58d88ee0312bfb0bc135b851df0928f");

        // We have both Private Key and Public Key for Alice here, but let's pretend we have only Public Key:
        Schnorrkel.PublicKey signer = new Schnorrkel.PublicKey(aliceKey.getPublicKey());
        // Verify the signature
        boolean valid = Schnorrkel.getInstance().verify(signature, message, signer);
        System.out.println("Valid: " + valid + " for pubkey " + Hex.encodeHexString(aliceKey.getPublicKey()));
    }

    public static void verifyWithAddress() throws DecoderException, SchnorrkelException {
        Address alice = Address.from("5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY");
        byte[] message = Hex.decodeHex(
                "8a3476995d076964c8c8517c8a1a504da91dc2b16ab36fb04ca22734e572619be108ee699592ccb9b1344835352e42c9"
        );
        byte[] signature = Hex.decodeHex("e2525d278d3d4b32ca3372b6d2c32c1405f641a5c2309a94da416c32359ac76e485c6baa69baa66def1c3a46c76fc38fb58d88ee0312bfb0bc135b851df0928f");

        // Public key actually is an Address. So lets say you have only address of the signer, like:
        Schnorrkel.PublicKey signer = new Schnorrkel.PublicKey(alice.getPubkey());
        // Verify the signature
        boolean valid = Schnorrkel.getInstance().verify(signature, message, signer);
        System.out.println("Valid: " + valid + " for address " + alice);
    }

    public static void main(String[] args) {
        try {
            createNewKey();
            System.out.println("\n---\n");
            derive();
            System.out.println("\n---\n");
            sign();
            System.out.println("\n---\n");
            verifyWithPubkey();
            System.out.println("\n---\n");
            verifyWithAddress();
        } catch (SchnorrkelException | DecoderException e) {
            e.printStackTrace();
        }
    }
}
