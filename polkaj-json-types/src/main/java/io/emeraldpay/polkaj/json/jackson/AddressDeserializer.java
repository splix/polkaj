package io.emeraldpay.polkaj.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.emeraldpay.polkaj.types.Address;

import java.io.IOException;

public class AddressDeserializer extends StdDeserializer<Address> {

    protected AddressDeserializer() {
        super(Address.class);
    }

    @Override
    public Address deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String value = p.readValueAs(String.class);
        if (value == null) {
            return null;
        }
        try {
            return Address.from(value);
        } catch (Exception e) {
            throw new InvalidFormatException(p, "Not an address: " + value, value, Address.class);
        }
    }
}
