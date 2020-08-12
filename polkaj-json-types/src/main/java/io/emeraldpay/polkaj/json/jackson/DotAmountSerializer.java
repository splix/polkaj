package io.emeraldpay.polkaj.json.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.emeraldpay.polkaj.types.DotAmount;

import java.io.IOException;

public class DotAmountSerializer extends StdSerializer<DotAmount> {

    protected DotAmountSerializer() {
        super(DotAmount.class);
    }

    @Override
    public void serialize(DotAmount value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeString(value.getValue().toString());
        }
    }
}
