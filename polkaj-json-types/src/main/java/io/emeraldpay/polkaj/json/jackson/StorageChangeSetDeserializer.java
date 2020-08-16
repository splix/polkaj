package io.emeraldpay.polkaj.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.emeraldpay.polkaj.json.StorageChangeSetJson;
import io.emeraldpay.polkaj.types.ByteData;

import java.io.IOException;

public class StorageChangeSetDeserializer {

    static class KeyValueOptionDeserializer extends StdDeserializer<StorageChangeSetJson.KeyValueOption> {

        protected KeyValueOptionDeserializer() {
            super(StorageChangeSetJson.KeyValueOption.class);
        }

        @Override
        public StorageChangeSetJson.KeyValueOption deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (p.currentToken() != JsonToken.START_ARRAY) {
                throw new InvalidFormatException(p, "Not an array", null, StorageChangeSetJson.KeyValueOption.class);
            }
            p.nextToken();
            if (p.currentToken() != JsonToken.VALUE_STRING) {
                throw new InvalidFormatException(p, "Not a string item for key", null, StorageChangeSetJson.KeyValueOption.class);
            }
            StorageChangeSetJson.KeyValueOption result = new StorageChangeSetJson.KeyValueOption();
            result.setKey(p.readValueAs(ByteData.class));
            p.nextToken();
            if (p.currentToken() != JsonToken.VALUE_STRING) {
                throw new InvalidFormatException(p, "Not a string item for data", null, StorageChangeSetJson.KeyValueOption.class);
            }
            result.setData(p.readValueAs(ByteData.class));
            return result;
        }
    }
}
