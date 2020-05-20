package io.emeraldpay.pjc.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.emeraldpay.pjc.types.Hash256;

import java.io.IOException;

public class Hash256Deserializer extends StdDeserializer<Hash256> {

    protected Hash256Deserializer() {
        super(Hash256.class);
    }

    @Override
    public Hash256 deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String hex = DeserializeCommons.getHexString(p);
        if (hex == null) {
            return null;
        }
        return Hash256.from(hex);
    }
}
