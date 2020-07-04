package io.emeraldpay.polkaj.api;

import java.util.concurrent.CompletableFuture;

/**
 * @see PolkadotMethod
 */
public interface PolkadotApi {

    /**
     * Execute JSON RPC request
     *
     * @param call call details to execute
     * @param <T> type of the result
     * @return CompletableFuture for the result. Note that the Future may throw RpcException when it get
     * @see RpcException
     */
    <T> CompletableFuture<T> execute(RpcCall<T> call);

    /**
     *
     * @return Standard Polkadot RPC commands
     */
    public static StandardCommands commands() {
        return StandardCommands.getInstance();
    }

}
