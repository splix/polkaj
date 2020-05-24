package io.emeraldpay.pjc.clientws;

import io.emeraldpay.pjc.client.RpcResponse;

/**
 * Container for the WebSocker message. A message may be a subscription event, or a response to a standard RPC call.
 *
 * @see io.emeraldpay.pjc.clientws.PolkadotWsClient.SubscriptionResponse
 * @see RpcResponse
 */
public class WsResponse {

    private final Type type;
    private final Object value;

    private WsResponse(Type type, Object value) {
        this.type = type;
        this.value = value;
    }

    /**
     *
     * @return Type of the response
     */
    public Type getType() {
        return type;
    }

    /**
     *
     * @return Value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Make sure the value is SubscriptionResponse and return it
     * @return value as event
     */
    public PolkadotWsClient.SubscriptionResponse<?> asEvent() {
        if (type != Type.SUBSCRIPTION) {
            throw new ClassCastException("Not an event");
        }
        return (PolkadotWsClient.SubscriptionResponse<?>) value;
    }

    /**
     * Make sure the value is RpcResponse and return it
     * @return value as rpc
     */
    public RpcResponse<?> asRpc() {
        if (type != Type.RPC) {
            throw new ClassCastException("Not an rpc");
        }
        return (RpcResponse<?>) value;
    }

    /**
     * Create new Response for Subscription
     *
     * @param event event data
     * @return response instance configured for Subscription Event
     */
    public static WsResponse subscription(PolkadotWsClient.SubscriptionResponse<?> event) {
        return new WsResponse(Type.SUBSCRIPTION, event);
    }

    /**
     * Create new Response for RPC
     * @param result RPC response
     * @return response instance configured for RPC
     */
    public static WsResponse rpc(RpcResponse<?> result) {
        return new WsResponse(Type.RPC, result);
    }

    /**
     * Type of Response
     */
    public enum Type {
        SUBSCRIPTION,
        RPC
    }

    /**
     * Pair of ID and associated Value
     */
    public static class IdValue {
        private final int id;
        private final Object value;

        public IdValue(int id, Object value) {
            this.id = id;
            this.value = value;
        }

        public int getId() {
            return id;
        }

        public Object getValue() {
            return value;
        }
    }

}
