package io.emeraldpay.polkaj.tx;

import io.emeraldpay.polkaj.api.PolkadotApi;
import io.emeraldpay.polkaj.api.StandardCommands;
import io.emeraldpay.polkaj.types.ByteData;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class StorageRequest<T> implements EncodeRequest, Function<ByteData, T> {

    @Override
    public abstract ByteData encodeRequest();

    public CompletableFuture<T> execute(PolkadotApi api) {
        return api.execute(
                StandardCommands.getInstance().stateGetStorage(encodeRequest())
        ).thenApply(this);
    }
}
