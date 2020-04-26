package io.emeraldpay.pjc.scale.writer;

import io.emeraldpay.pjc.scale.ScaleWriter;
import io.emeraldpay.pjc.scale.ScaleCodecWriter;

import java.io.IOException;

public class UInt16Writer implements ScaleWriter<Integer> {
    @Override
    public void write(ScaleCodecWriter wrt, Integer value) throws IOException {
        wrt.write(value & 0xff);
        wrt.write((value >> 8) & 0xff);
    }
}
