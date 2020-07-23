package io.emeraldpay.polkaj.tx;

import net.openhft.hashing.LongHashFunction;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * <ul>
 *     <li><a href="https://cyan4973.github.io/xxHash/">xsHash</a></li>
 *     <li><a href="https://github.com/OpenHFT/Zero-Allocation-Hashing">OpenHFT Zero-Allocation-Hashing</a></li>
 * </ul>
 */
public class Hashing {

    /**
     * Hash with xxhash algorithm. Produces 128 bits output. The xxhash by default gives only 64 bytes, so
     * the xxhash128 applies it twice with seed 0 and seed 1
     *
     * @param value string to hash
     * @return 128 bit (16 bytes) hash of the string
     */
    public static byte[] xxhash128(String value) {
        byte[] valueBytes = value.getBytes();
        ByteBuffer buf = ByteBuffer.allocate(16)
                .order(ByteOrder.LITTLE_ENDIAN);
        buf.asLongBuffer()
                .put(LongHashFunction.xx(0).hashBytes(valueBytes))
                .put(LongHashFunction.xx(1).hashBytes(valueBytes));
        return buf.flip().array();
    }
}
