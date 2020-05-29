package io.emeraldpay.polkaj.types;

import io.emeraldpay.polkaj.ss58.SS58Type;
import io.emeraldpay.polkaj.ss58.SS58;
import io.emeraldpay.polkaj.ss58.SS58Codec;

import java.util.Arrays;
import java.util.Objects;

/**
 * Polkadot Address
 */
public class Address implements Comparable<Address> {

    public static final int SIZE_BYTES = 32;

    private final byte[] pubkey;
    private final SS58Type.Network network;
    private transient String encoded;

    public Address(SS58Type.Network network, byte[] pubkey) {
        if (network == null) {
            throw new NullPointerException("Network is null");
        }
        if (pubkey == null) {
            throw new NullPointerException("Pubkey is null");
        }
        if (pubkey.length != SIZE_BYTES) {
            throw new IllegalArgumentException("PubKey length should be 32 bytes long. Provided: " + pubkey.length);
        }
        this.network = network;
        this.pubkey = pubkey.clone();
    }

    /**
     * Creates a zero pubkey address
     * @param network target network
     * @return address
     */
    public static Address empty(SS58Type.Network network) {
        return new Address(network, new byte[32]);
    }

    public static Address from(String address) {
        SS58 decoded = SS58Codec.getInstance().decode(address);
        SS58Type.Network type = SS58Type.Network.from(decoded.getType().getValue());
        return new Address(type, decoded.getValue());
    }

    public byte[] getPubkey() {
        return pubkey;
    }

    public SS58Type.Network getNetwork() {
        return network;
    }

    public String toString() {
        if (encoded != null) {
            return encoded;
        }
        String encoded = SS58Codec.getInstance().encode(network, pubkey);
        this.encoded = encoded;
        return encoded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address)) return false;
        Address address = (Address) o;
        return Arrays.equals(pubkey, address.pubkey) &&
                Objects.equals(network, address.network);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(network);
        result = 31 * result + Arrays.hashCode(pubkey);
        return result;
    }

    @Override
    public int compareTo(Address o) {
        return this.toString().compareTo(o.toString());
    }
}
