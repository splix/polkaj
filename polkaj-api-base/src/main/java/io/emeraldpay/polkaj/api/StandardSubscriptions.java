package io.emeraldpay.polkaj.api;

import io.emeraldpay.polkaj.json.BlockJson;
import io.emeraldpay.polkaj.json.RuntimeVersionJson;
import io.emeraldpay.polkaj.json.StorageChangeSetJson;
import io.emeraldpay.polkaj.types.ByteData;

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
        return SubscribeCall.create(BlockJson.Header.class, "chain_subscribeNewHead", "chain_unsubscribeNewHead");
    }

    /**
     * Subscribe to finalized headers
     *
     * @return command
     */
    public SubscribeCall<BlockJson.Header> finalizedHeads() {
        return SubscribeCall.create(BlockJson.Header.class, "chain_subscribeFinalizedHeads", "chain_unsubscribeFinalizedHeads");
    }

    /**
     * Subscribe to new runtime versions
     *
     * @return command
     */
    public SubscribeCall<RuntimeVersionJson> runtimeVersion() {
        return SubscribeCall.create(RuntimeVersionJson.class, "state_subscribeRuntimeVersion", "state_unsubscribeRuntimeVersion");
    }

    public SubscribeCall<StorageChangeSetJson> storage() {
        return SubscribeCall.create(StorageChangeSetJson.class, "state_subscribeStorage", "state_unsubscribeStorage");
    }

    public SubscribeCall<StorageChangeSetJson> storage(List<ByteData> keys) {
        if (keys == null || keys.isEmpty()) {
            return storage();
        }
        return SubscribeCall.create(StorageChangeSetJson.class, "state_subscribeStorage", "state_unsubscribeStorage", List.of(keys));
    }
}
