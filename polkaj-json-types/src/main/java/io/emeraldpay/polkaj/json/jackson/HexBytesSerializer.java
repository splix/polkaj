package io.emeraldpay.polkaj.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.emeraldpay.polkaj.types.ByteData;

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
