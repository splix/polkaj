package io.emeraldpay.polkaj.api;

import io.emeraldpay.polkaj.json.BlockJson;
import io.emeraldpay.polkaj.json.RuntimeVersionJson;
import io.emeraldpay.polkaj.json.StorageChangeSetJson;
import io.emeraldpay.polkaj.types.ByteData;

import java.util.Collections;
import java.util.List;

/**
 * Standard/common Polkadot subscriptions
 */
public class StandardSubscriptions {

    private static final StandardSubscriptions instance = new StandardSubscriptions();

    public static StandardSubscriptions getInstance() {
        return instance;
    }

    /**
     * Subscribe to new headers
     *
     * @return command
     */
    public SubscribeCall<BlockJson.Header> newHeads() {
        return SubscribeCall.create(BlockJson.Header.class, PolkadotMethod.CHAIN_SUBSCRIBE_NEW_HEADS, PolkadotMethod.CHAIN_UNSUBSCRIBE_NEW_HEADS);
    }

    /**
     * Subscribe to finalized headers
     *
     * @return command
     */
    public SubscribeCall<BlockJson.Header> finalizedHeads() {
        return SubscribeCall.create(BlockJson.Header.class, PolkadotMethod.CHAIN_SUBSCRIBE_FINALIZED_HEADS, PolkadotMethod.CHAIN_UNSUBSCRIBE_FINALIZED_HEADS);
    }

    /**
     * Subscribe to new runtime versions
     *
     * @return command
     */
    public SubscribeCall<RuntimeVersionJson> runtimeVersion() {
        return SubscribeCall.create(RuntimeVersionJson.class, PolkadotMethod.STATE_SUBSCRIBE_RUNTIME_VERSION, PolkadotMethod.STATE_UNSUBSCRIBE_RUNTIME_VERSION);
    }

    public SubscribeCall<StorageChangeSetJson> storage() {
        return SubscribeCall.create(StorageChangeSetJson.class, PolkadotMethod.STATE_SUBSCRIBE_STORAGE, PolkadotMethod.STATE_UNSUBSCRIBE_STORAGE);
    }

    public SubscribeCall<StorageChangeSetJson> storage(List<ByteData> keys) {
        if (keys == null) {
            throw new NullPointerException("Storage keys list is null");
        }
        if (keys.isEmpty()) {
            return storage();
        }
        return SubscribeCall.create(StorageChangeSetJson.class, PolkadotMethod.STATE_SUBSCRIBE_STORAGE, PolkadotMethod.STATE_UNSUBSCRIBE_STORAGE, Collections.unmodifiableList(keys));
    }

    public SubscribeCall<StorageChangeSetJson> storage(ByteData key) {
        if (key == null) {
            throw new NullPointerException("Storage key is null");
        }
        return storage(Collections.singletonList(key));
    }
}
