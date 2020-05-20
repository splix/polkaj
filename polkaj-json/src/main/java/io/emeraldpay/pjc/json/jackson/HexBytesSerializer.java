package io.emeraldpay.pjc.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.emeraldpay.pjc.types.ByteData;

import java.io.IOException;

public class HexBytesSerializer extends StdSerializer<ByteData> {
    protected HexBytesSerializer() {
        super(ByteData.class);
    }

    @Override
    public void serialize(ByteData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeString(value.toString());
        }
    }
}
