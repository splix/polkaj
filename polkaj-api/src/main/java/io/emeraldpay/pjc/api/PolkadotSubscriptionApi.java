package io.emeraldpay.pjc.api;

import java.util.concurrent.CompletableFuture;

public interface PolkadotSubscriptionApi {

    /**
     * Subscribe to a method that provides multiple responses
     * <br>
     * For example method <code>chain_subscribeNewHead</code> provides subscription to new blocks (represented with <code>BlockJson.Header</code>)
     * And to cancel the subscription you need to execute <code>chain_unsubscribeNewHead</code>
     *
     * @param clazz type of the data provided with the subsription
     * @param method method used to subscribe
     * @param unsubscribeMethod method used to unsubscribe
     * @param params parameters for subscription
     * @param <T> type
     * @return Subscription instance
     */
    <T> CompletableFuture<Subscription<T>> subscribe(Class<T> clazz, String method, String unsubscribeMethod, Object... params);
}
