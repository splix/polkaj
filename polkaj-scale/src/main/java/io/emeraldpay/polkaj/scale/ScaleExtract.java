package io.emeraldpay.polkaj.scale;

import java.util.function.Function;

/**
 * Common shortcuts for SCALE extract
 */
public class ScaleExtract {

    /**
     * Shortcut to setup extraction of an Object from bytes value
     *
     * @param reader actual reader to use
     * @param <T> type of the result
     * @return Function to apply for extraction
     */
    public static <T> Function<byte[], T> fromBytes(ScaleReader<T> reader) {
        if (reader == null) {
            throw new NullPointerException("ScaleReader is null");
        }
        return (encoded) -> {
            ScaleCodecReader codec = new ScaleCodecReader(encoded);
            return codec.read(reader);
        };
    }
}
