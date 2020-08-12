package io.emeraldpay.polkaj.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.emeraldpay.polkaj.types.DotAmount;

import java.io.IOException;
import java.math.BigInteger;

public class DotAmountDeserializer extends StdDeserializer<DotAmount> {

    protected DotAmountDeserializer() {
        super(DotAmount.class);
    }

    @Override
    public DotAmount deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String value = p.readValueAs(String.class);
        if (value == null) {
            return null;
        }
        try {
            return new DotAmount(new BigInteger(value));
        } catch (Exception e) {
            throw new InvalidFormatException(p, "Not a number: " + value, value, DotAmount.class);
        }
    }
}
