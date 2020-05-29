package io.emeraldpay.polkaj.scale.reader;

import io.emeraldpay.polkaj.scale.ScaleReader;
import io.emeraldpay.polkaj.scale.ScaleCodecReader;

public class BoolReader implements ScaleReader<Boolean> {
    @Override
    public Boolean read(ScaleCodecReader rdr) {
        byte b = rdr.readByte();
        if (b == 0) {
            return false;
        }
        if (b == 1) {
            return true;
        }
        throw new IllegalStateException("Not a boolean value: " + b);
    }
}
