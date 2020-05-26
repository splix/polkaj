package io.emeraldpay.pjc.api;

import java.util.concurrent.CompletableFuture;

public interface PolkadotApi {

    /**
     * Execute JSON RPC request
     *
     * @param clazz expected resulting class
     * @param method method name
     * @param params params to the method
     * @param <T> type of the result
     * @return CompletableFuture for the result. Note that the Future may throw RpcException when it get
     * @see RpcException
     */
    <T> CompletableFuture<T> execute(Class<T> clazz, String method, Object... params);

}
