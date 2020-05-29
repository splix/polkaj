package io.emeraldpay.polkaj.scale.writer;

import io.emeraldpay.polkaj.scale.CompactMode;
import io.emeraldpay.polkaj.scale.ScaleWriter;
import io.emeraldpay.polkaj.scale.ScaleCodecWriter;

import java.io.IOException;

public class CompactUIntWriter implements ScaleWriter<Integer> {

    @Override
    public void write(ScaleCodecWriter wrt, Integer value) throws IOException {
        CompactMode mode = CompactMode.forNumber(value);
        int compact;
        int bytes;
        if (mode == CompactMode.BIGINT) {
            wrt.directWrite(mode.getValue());
            compact = value;
            bytes = 4;
        } else {
            compact = (value << 2) + mode.getValue();
            if (mode == CompactMode.SINGLE) {
                bytes = 1;
            } else if (mode == CompactMode.TWO) {
                bytes = 2;
            } else {
                bytes = 4;
            }
        }
        while (bytes > 0) {
            wrt.directWrite(compact & 0xff);
            compact >>= 8;
            bytes--;
        }
    }

}
