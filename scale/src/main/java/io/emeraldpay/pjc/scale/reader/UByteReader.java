package io.emeraldpay.pjc.scale.reader;

import io.emeraldpay.pjc.scale.ItemReader;
import io.emeraldpay.pjc.scale.ScaleCodecReader;

public class UByteReader implements ItemReader<Integer> {
    @Override
    public Integer read(ScaleCodecReader rdr) {
        byte x = rdr.readByte();
        if (x < 0) {
            return 256 + (int)x;
        }
        return (int)x;
    }
}
