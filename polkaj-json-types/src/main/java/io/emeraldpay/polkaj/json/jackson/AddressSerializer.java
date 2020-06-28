package io.emeraldpay.polkaj.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.emeraldpay.polkaj.types.Address;

import java.io.IOException;

public class AddressSerializer extends StdSerializer<Address> {

    public AddressSerializer() {
        super(Address.class);
    }

    @Override
    public void serialize(Address value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeString(value.toString());
        }
    }
}
