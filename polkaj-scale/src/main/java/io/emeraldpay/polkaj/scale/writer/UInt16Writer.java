package io.emeraldpay.polkaj.scale.writer;

import io.emeraldpay.polkaj.scale.ScaleWriter;
import io.emeraldpay.polkaj.scale.ScaleCodecWriter;

import java.io.IOException;

public class UInt16Writer implements ScaleWriter<Integer> {
    @Override
    public void write(ScaleCodecWriter wrt, Integer value) throws IOException {
        wrt.directWrite(value & 0xff);
        wrt.directWrite((value >> 8) & 0xff);
    }
}
