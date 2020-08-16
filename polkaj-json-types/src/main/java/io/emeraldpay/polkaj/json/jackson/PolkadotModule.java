package io.emeraldpay.polkaj.json.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.emeraldpay.polkaj.json.StorageChangeSetJson;
import io.emeraldpay.polkaj.types.Address;
import io.emeraldpay.polkaj.types.ByteData;
import io.emeraldpay.polkaj.types.DotAmount;
import io.emeraldpay.polkaj.types.Hash256;

public class PolkadotModule extends SimpleModule {

    public PolkadotModule() {
        super();
        addDeserializer(Hash256.class, new Hash256Deserializer());
        addSerializer(Hash256.class, new Hash256Serializer());
        addDeserializer(ByteData.class, new HexBytesDeserializer());
        addSerializer(ByteData.class, new HexBytesSerializer());
        addDeserializer(Address.class, new AddressDeserializer());
        addSerializer(Address.class, new AddressSerializer());
        addDeserializer(DotAmount.class, new DotAmountDeserializer());
        addSerializer(DotAmount.class, new DotAmountSerializer());

        addDeserializer(StorageChangeSetJson.KeyValueOption.class,
                new StorageChangeSetDeserializer.KeyValueOptionDeserializer());
    }
}
