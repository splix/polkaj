package io.emeraldpay.pjc.api;

import io.emeraldpay.pjc.json.BlockJson;

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
}
