package io.emeraldpay.pjc.scale.writer;

import io.emeraldpay.pjc.scale.ScaleWriter;
import io.emeraldpay.pjc.scale.ScaleCodecWriter;

import java.io.IOException;
import java.util.Optional;

public class BoolOptionalWriter implements ScaleWriter<Optional<Boolean>> {

    @Override
    public void write(ScaleCodecWriter wrt, Optional<Boolean> value) throws IOException {
        if (value.isEmpty()) {
            wrt.write(0);
        } else if (value.get()) {
            wrt.write(2);
        } else {
            wrt.write(1);
        }
    }
}
