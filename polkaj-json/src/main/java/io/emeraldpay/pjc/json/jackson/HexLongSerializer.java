package io.emeraldpay.pjc.json.jackson;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class HexLongSerializer extends StdSerializer<Long> {

    protected HexLongSerializer() {
        super(Long.class);
    }

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            if (value < 0) {
                throw new JsonGenerationException("Serializing negative numbers as hex is not supported", gen);
            }
            gen.writeString("0x" + Long.toHexString(value));
        }
    }
}
