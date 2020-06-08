package io.emeraldpay.polkaj.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.emeraldpay.polkaj.types.ByteData;

import java.io.IOException;

public class HexBytesDeserializer extends StdDeserializer<ByteData> {

    protected HexBytesDeserializer() {
        super(ByteData.class);
    }

    @Override
    public ByteData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String hex = p.readValueAs(String.class);
        if (hex == null || hex.length() == 0 || !hex.startsWith("0x")) {
            return null;
        }
        return ByteData.from(hex);
    }
}
