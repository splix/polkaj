package io.emeraldpay.pjc.scale.reader;

import io.emeraldpay.pjc.scale.ItemReader;
import io.emeraldpay.pjc.scale.ScaleCodecReader;

public class BoolReader implements ItemReader<Boolean> {
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
