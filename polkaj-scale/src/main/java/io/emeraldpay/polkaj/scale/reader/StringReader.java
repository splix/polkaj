package io.emeraldpay.polkaj.scale.reader;

import io.emeraldpay.polkaj.scale.ScaleCodecReader;
import io.emeraldpay.polkaj.scale.ScaleReader;

/**
 * Read string, encoded as UTF-8 bytes
 */
public class StringReader implements ScaleReader<String> {
    @Override
    public String read(ScaleCodecReader rdr) {
        return rdr.readString();
    }
}
