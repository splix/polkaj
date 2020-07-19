import io.emeraldpay.polkaj.schnorrkel.Schnorrkel;
import io.emeraldpay.polkaj.schnorrkel.SchnorrkelException;
import io.emeraldpay.polkaj.ss58.SS58Type;
import io.emeraldpay.polkaj.types.Address;
import org.apache.commons.codec.binary.Hex;

public class Keys {

    public static void createNewKey() throws SchnorrkelException {
        System.out.println("Generate a new Root Key + derive a `demo` address from that key");
        System.out.println("");
        Schnorrkel.KeyPair rootKey = Schnorrkel.generateKeyPair();
        System.out.println("  Root Key: " + Hex.encodeHexString(rootKey.getSecretKey()));
        System.out.println("");
        Schnorrkel.KeyPair key = Schnorrkel.deriveKeyPair(rootKey, Schnorrkel.ChainCode.from("demo".getBytes()));

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

        Schnorrkel.PublicKey derived = Schnorrkel.derivePublicKeySoft(publicKey, Schnorrkel.ChainCode.from("demo".getBytes()));
        Address anotherAddress = new Address(SS58Type.Network.CANARY, derived.getPublicKey());
        System.out.println("   Address (new): " + anotherAddress);
        System.out.println("Public Key (new): " + Hex.encodeHexString(derived.getPublicKey()));
    }

    public static void main(String[] args) {
        try {
            createNewKey();
            System.out.println("");
            System.out.println("---");
            System.out.println("");
            derive();
        } catch (SchnorrkelException e) {
            e.printStackTrace();
        }
    }
}
