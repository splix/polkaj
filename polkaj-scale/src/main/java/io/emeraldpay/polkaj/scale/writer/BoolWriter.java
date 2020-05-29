package io.emeraldpay.polkaj.scale.writer;

import io.emeraldpay.polkaj.scale.ScaleWriter;
import io.emeraldpay.polkaj.scale.ScaleCodecWriter;

import java.io.IOException;

public class BoolWriter implements ScaleWriter<Boolean> {
    @Override
    public void write(ScaleCodecWriter wrt, Boolean value) throws IOException {
        if (value) {
            wrt.directWrite(1);
        } else {
            wrt.directWrite(0);
        }
    }
}
