package io.emeraldpay.pjc.scale.writer;

import io.emeraldpay.pjc.scale.ScaleWriter;
import io.emeraldpay.pjc.scale.ScaleCodecWriter;

import java.io.IOException;

public class UInt32Writer implements ScaleWriter<Integer> {
    @Override
    public void write(ScaleCodecWriter wrt, Integer value) throws IOException {
        if (value < 0) {
            throw new IllegalArgumentException("Negative values are not supported: " + value);
        }
        wrt.write(value & 0xff);
        wrt.write((value >> 8) & 0xff);
        wrt.write((value >> 16) & 0xff);
        wrt.write((value >> 24) & 0xff);
    }
}
