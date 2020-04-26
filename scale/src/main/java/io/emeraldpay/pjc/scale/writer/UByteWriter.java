package io.emeraldpay.pjc.scale.writer;

import io.emeraldpay.pjc.scale.ScaleWriter;
import io.emeraldpay.pjc.scale.ScaleCodecWriter;

import java.io.IOException;

public class UByteWriter implements ScaleWriter<Integer> {

    @Override
    public void write(ScaleCodecWriter wrt, Integer value) throws IOException {
        if (value < 0 || value > 0xff) {
            throw new IllegalArgumentException("Only values in range 0..255 are supported: " + value);
        }
        wrt.write(value);
    }
}
