package io.emeraldpay.polkaj.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.emeraldpay.polkaj.types.Hash256;

import java.io.IOException;

public class Hash256Serializer extends StdSerializer<Hash256> {
    protected Hash256Serializer() {
        super(Hash256.class);
    }

    @Override
    public void serialize(Hash256 value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeString(value.toString());
        }
    }
}
