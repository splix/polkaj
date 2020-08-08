package io.emeraldpay.polkaj.tx;

import io.emeraldpay.polkaj.types.Address;
import net.openhft.hashing.LongHashFunction;
import org.bouncycastle.crypto.digests.Blake2bDigest;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <ul>
 *     <li><a href="https://cyan4973.github.io/xxHash/">xxHash</a></li>
 *     <li><a href="https://github.com/OpenHFT/Zero-Allocation-Hashing">OpenHFT Zero-Allocation-Hashing</a></li>
 *     <li><a href="https://blake2.net/">Blake2</a></li>
 * </ul>
 */
public class Hashing {

    /**
     * Hash with xxhash algorithm. Produces 128 bits output. The xxhash by default gives only 64 bytes, so
     * the xxhash128 applies it twice with seed 0 and seed 1
     *
     * @param buf buffer to put the result
     * @param value string to hash
     */
    public static void xxhash128(ByteBuffer buf, String value) {
        byte[] valueBytes = value.getBytes();
        ByteOrder oder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.asLongBuffer()
                .put(LongHashFunction.xx(0).hashBytes(valueBytes))
                .put(LongHashFunction.xx(1).hashBytes(valueBytes));
        buf.position(buf.position() + 16);
        buf.order(oder);
    }

    /**
     * Hash with Blake2 256 bit
     *
     * @param buf buffer to put the result
     * @param value value to hash
     */
    public static void blake2(ByteBuffer buf, byte[] value) {
        buf.put(blake2(value));
    }

    /**
     * Hash with Blake2 256 bit
     *
     * @param value value to hash
     * @return hash of the value
     */
    public static byte[] blake2(byte[] value) {
        Blake2bDigest digest = new Blake2bDigest(256);
        digest.update(value, 0, value.length);

        byte[] result = new byte[32];
        digest.doFinal(result, 0);
        return result;
    }


    /**
     * Hash address with Blake2 256 bit. Uses Public Key for the hash input, i.e. Address Network is not included.
     *
     * @param buf buffer to put the result
     * @param value value to hash
     */
    public static void blake2(ByteBuffer buf, Address value) {
        blake2(buf, value.getPubkey());
    }

    /**
     * Hash with Blake2 128 bit
     *
     * @param buf buffer to put the result
     * @param value value to hash
     */
    public static void blake2128(ByteBuffer buf, byte[] value) {
        Blake2bDigest digest = new Blake2bDigest(128);
        digest.update(value, 0, value.length);

        byte[] result = new byte[16];
        digest.doFinal(result, 0);

        buf.put(result);
    }

    /**
     * Hash address with Blake2 128 bit. Uses Public Key for the hash input, i.e. Address Network is not included.
     *
     * @param buf buffer to put the result
     * @param value value to hash
     */
    public static void blake2128(ByteBuffer buf, Address value) {
        blake2128(buf, value.getPubkey());
    }
}
