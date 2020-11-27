package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.scale.ScaleCodecReader;
import io.emeraldpay.polkaj.scale.ScaleReader;

public class EraReader implements ScaleReader<Integer> {
    @Override
    public Integer read(ScaleCodecReader rdr) {
        byte low = rdr.readByte();
        if (low != 0) {
            byte high = rdr.readByte();
            return Byte.toUnsignedInt(high) << 8 | Byte.toUnsignedInt(low);
        } else {
            return 0;
        }
    }
}
