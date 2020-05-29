package io.emeraldpay.polkaj.api;

public class RpcException extends RuntimeException {

    private final int code;
    private final String rpcMessage;

    public RpcException(int code, String rpcMessage) {
        this(code, rpcMessage, null);
    }

    public RpcException(int code, String rpcMessage, Throwable e) {
        super("RPC Exception " + code + ": " + rpcMessage, e);
        this.code = code;
        this.rpcMessage = rpcMessage;
    }

    public int getCode() {
        return code;
    }

    public String getRpcMessage() {
        return rpcMessage;
    }
}
