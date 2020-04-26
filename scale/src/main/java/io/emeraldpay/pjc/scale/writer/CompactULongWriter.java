package io.emeraldpay.pjc.scale.writer;

import io.emeraldpay.pjc.scale.CompactMode;
import io.emeraldpay.pjc.scale.ScaleCodecWriter;
import io.emeraldpay.pjc.scale.ScaleWriter;

import java.io.IOException;
import java.math.BigInteger;

public class CompactULongWriter implements ScaleWriter<Long> {

    private static final CompactBigIntWriter BIGINT_WRITER = new CompactBigIntWriter();

    @Override
    public void write(ScaleCodecWriter wrt, Long value) throws IOException {
        CompactMode mode = CompactMode.forNumber(value);
        long compact;
        int bytes;
        if (mode == CompactMode.BIGINT) {
            BIGINT_WRITER.write(wrt, BigInteger.valueOf(value));
            return;
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
            wrt.write((int)compact & 0xff);
            compact >>= 8;
            bytes--;
        }
    }
}
