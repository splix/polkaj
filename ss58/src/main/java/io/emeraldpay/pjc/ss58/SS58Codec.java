package io.emeraldpay.pjc.ss58;

import io.ipfs.multibase.Base58;
import org.bouncycastle.jcajce.provider.digest.Blake2b;

/**
 * Encode values with SS58 encoding (checksummed Base58)
 */
public class SS58Codec {

    private static final SS58Codec DEFAULT = new SS58Codec();

    private static final byte[] CHKSUM_PREFIX = "SS58PRE".getBytes();
    private static final int CHECKSUM_LEN = 2;
    private static final int TYPE_LEN = 1;

    public static SS58Codec getInstance() {
        return DEFAULT;
    }

    /**
     * Encode address (public key) with SS58
     * @param SS58Type type of the value
     * @param value value
     *
     * @return encoding string
     * @throws IllegalArgumentException if address or pubkey is null, or pubkey is invalid
     */
    public String encode(SS58Type SS58Type, byte[] value) {
        if (SS58Type == null) {
            throw new IllegalArgumentException("AddressType is null");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        if (value.length != 32) {
            //TODO what if some different type, not pubkey?
            throw new IllegalArgumentException("Value length is expected to be 32 bytes, but has: " + value.length);
        }
        byte[] checksumSource = new byte[CHKSUM_PREFIX.length + TYPE_LEN + value.length];
        System.arraycopy(CHKSUM_PREFIX, 0, checksumSource, 0, CHKSUM_PREFIX.length);
        checksumSource[CHKSUM_PREFIX.length] = SS58Type.getValue();
        System.arraycopy(value, 0, checksumSource, CHKSUM_PREFIX.length + TYPE_LEN, value.length);

        //spec says it's 256, but in reality it's 512
        Blake2b.Blake2b512 hash = new Blake2b.Blake2b512();
        byte[] checksum = hash.digest(checksumSource);

        byte[] result = new byte[TYPE_LEN + value.length + CHECKSUM_LEN];
        result[0] = SS58Type.getValue();
        System.arraycopy(value, 0, result, TYPE_LEN, value.length);
        System.arraycopy(checksum, 0, result, TYPE_LEN + value.length, 2);

        return Base58.encode(result);
    }

    /**
     * Decode and verify value (pubkey)
     *
     * @param value SS58 encoded address
     * @return value
     * @throws IllegalArgumentException if input value is invalid or has incorrect checksum
     */
    public SS58 decode(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Input value is null");
        }
        if (value.length() == 0) {
            throw new IllegalArgumentException("Input value is too short");
        }

        byte[] decoded = Base58.decode(value);
        //should have at least 1 byte of actual data
        if (decoded.length < TYPE_LEN + CHECKSUM_LEN + 1) {
            throw new IllegalArgumentException("Input value is too short");
        }

        int pubKeyLength = decoded.length - CHECKSUM_LEN - TYPE_LEN;
        byte[] checksumSource = new byte[CHKSUM_PREFIX.length + TYPE_LEN + pubKeyLength];
        System.arraycopy(CHKSUM_PREFIX, 0, checksumSource, 0, CHKSUM_PREFIX.length);
        System.arraycopy(decoded, 0, checksumSource, CHKSUM_PREFIX.length, pubKeyLength + TYPE_LEN);

        Blake2b.Blake2b512 hash = new Blake2b.Blake2b512();
        byte[] checksum = hash.digest(checksumSource);
        if (checksum[0] != decoded[decoded.length - CHECKSUM_LEN] || checksum[1] != decoded[decoded.length - CHECKSUM_LEN + 1]) {
            throw new IllegalArgumentException("Incorrect checksum");
        }

        byte[] pubkey = new byte[pubKeyLength];
        System.arraycopy(decoded, TYPE_LEN, pubkey, 0, pubKeyLength);

        return new SS58(SS58Type.Network.from(decoded[0]), pubkey, checksum);
    }
}
