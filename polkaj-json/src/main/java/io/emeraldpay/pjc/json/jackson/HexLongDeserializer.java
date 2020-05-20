package io.emeraldpay.pjc.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class HexLongDeserializer extends StdDeserializer<Long> {

    protected HexLongDeserializer() {
        super(Long.class);
    }

    @Override
    public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String hex = DeserializeCommons.getHexString(p);
        if (hex == null) {
            return null;
        }
        hex = hex.substring(2);
        return Long.parseLong(hex, 16);
    }
}
