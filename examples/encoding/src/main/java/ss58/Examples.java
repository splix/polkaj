package ss58;

import io.emeraldpay.polkaj.ss58.SS58;
import io.emeraldpay.polkaj.ss58.SS58Codec;
import io.emeraldpay.polkaj.ss58.SS58Type;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class Examples {

    public static void main(String[] args) throws DecoderException {
        encode();
        decode();
    }

    public static void encode() throws DecoderException {
        byte[] pubkey = Hex.decodeHex(
                // a pubkey is 32 byte value, for this example it's hardcoded as hex
                "9053cc32597892cc2cd43ea6e3c0db7a3b4c52e5fe6052762080dbc3e3222c0b"
        );
        String address = SS58Codec.getInstance().encode(
                // using Kusama here. but for Polkadot mainnet use SS58Type.Network.LIVE
                SS58Type.Network.CANARY,
                // pubkey as bytes
                pubkey
        );
        System.out.println("Address: " + address);
    }

    public static void decode() {
        SS58 address = SS58Codec.getInstance().decode("FqZJib4Kz759A1VFd2cXX4paQB42w7Uamsyhi4z3kGgCkQy");

        if (address.getType() != SS58Type.Network.CANARY) {
            throw new IllegalStateException("Not Kusama address");
        }

        System.out.println(
                "Pub key: " + Hex.encodeHexString(address.getValue())
        );
    }

}
