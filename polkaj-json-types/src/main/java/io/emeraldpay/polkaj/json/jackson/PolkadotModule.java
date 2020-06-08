package io.emeraldpay.polkaj.json.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.emeraldpay.polkaj.types.ByteData;
import io.emeraldpay.polkaj.types.Hash256;

public class PolkadotModule extends SimpleModule {

    public PolkadotModule() {
        super();
        addDeserializer(Hash256.class, new Hash256Deserializer());
        addSerializer(Hash256.class, new Hash256Serializer());
        addDeserializer(ByteData.class, new HexBytesDeserializer());
        addSerializer(ByteData.class, new HexBytesSerializer());
    }
}
