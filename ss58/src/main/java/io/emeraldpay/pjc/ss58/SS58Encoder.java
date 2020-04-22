package io.emeraldpay.pjc.ss58;

import io.ipfs.multibase.Base58;
import org.bouncycastle.jcajce.provider.digest.Blake2b;

/**
 * Encode values with SS58 encoding (checksummed Base58)
 */
public class SS58Encoder {

    private static SS58Encoder DEFAULT = new SS58Encoder();

    private static byte[] CHKSUM_PREFIX = "SS58PRE".getBytes();

    public static SS58Encoder getInstance() {
        return DEFAULT;
    }

    /**
     * Encode address (public key) with SS58
     * @param addressType type of the address
     * @param pubKey pubkey
     *
     * @return encoding string
     */
    public String encode(AddressType addressType, byte[] pubKey) {
        if (addressType == null) {
            throw new IllegalArgumentException("AddressType is null");
        }
        if (pubKey == null) {
            throw new IllegalArgumentException("PubKey is null");
        }
        if (pubKey.length != 32) {
            throw new IllegalArgumentException("PubKey length is expected to be 32 bytes, but has: " + pubKey.length);
        }
        byte[] checksumSource = new byte[CHKSUM_PREFIX.length + 1 + pubKey.length];
        System.arraycopy(CHKSUM_PREFIX, 0, checksumSource, 0, CHKSUM_PREFIX.length);
        checksumSource[CHKSUM_PREFIX.length] = addressType.getValue();
        System.arraycopy(pubKey, 0, checksumSource, 8, pubKey.length);

        //spec says it's 256, but in reality it's 512
        Blake2b.Blake2b512 hash = new Blake2b.Blake2b512();
        byte[] checksum = hash.digest(checksumSource);

        byte[] result = new byte[1 + pubKey.length + 2];
        result[0] = addressType.getValue();
        System.arraycopy(pubKey, 0, result, 1, pubKey.length);
        System.arraycopy(checksum, 0, result, 1 + pubKey.length, 2);

        return Base58.encode(result);
    }
}
