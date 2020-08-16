package io.emeraldpay.polkaj.tx;

import io.emeraldpay.polkaj.api.PolkadotApi;
import io.emeraldpay.polkaj.api.StandardCommands;
import io.emeraldpay.polkaj.types.ByteData;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class StorageRequest<T> implements EncodeRequest, Function<ByteData, T> {

    private transient ByteData encoded;

    @Override
    public abstract ByteData encodeRequest();

    /**
     * Execute with API and convert result to Java class
     *
     * @param api connected api
     * @return future to the result of execution
     */
    public CompletableFuture<T> execute(PolkadotApi api) {
        return api.execute(
                StandardCommands.getInstance().stateGetStorage(encodeRequest())
        ).thenApply(this);
    }

    /**
     * Compare to a storage key
     *
     * @param key another key
     * @return true if keys are equal
     */
    public boolean isKeyEqualTo(ByteData key) {
        ByteData encoded = this.encoded;
        if (encoded == null) {
            encoded = this.encodeRequest();
            this.encoded = encoded;
        }
        return encoded.equals(key);
    }
}
