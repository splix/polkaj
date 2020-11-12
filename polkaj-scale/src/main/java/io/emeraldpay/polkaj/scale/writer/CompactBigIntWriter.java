package io.emeraldpay.polkaj.scale.writer;

import io.emeraldpay.polkaj.scale.CompactMode;
import io.emeraldpay.polkaj.scale.ScaleCodecWriter;
import io.emeraldpay.polkaj.scale.ScaleWriter;

import java.io.IOException;
import java.math.BigInteger;

public class CompactBigIntWriter implements ScaleWriter<BigInteger> {

    private static final CompactULongWriter LONG_WRITER = new CompactULongWriter();

    @Override
    public void write(ScaleCodecWriter wrt, BigInteger value) throws IOException {
        CompactMode mode = CompactMode.forNumber(value);

        byte[] data = value.toByteArray();
        int length = data.length;
        int pos = data.length-1;
        int limit = 0;

        if (mode != CompactMode.BIGINT) {
            LONG_WRITER.write(wrt, value.longValue());
            return;
        }

        // skip the first byte if it's 0
        if (data[0]==0x00) {
            length--;
            limit++;
        }

        wrt.directWrite(((length - 4) << 2) + mode.getValue());
        while (pos >= limit) {
            wrt.directWrite(data[pos]);
            pos--;
        }
    }
}
