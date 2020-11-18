package io.emeraldpay.polkaj.api;

public class RpcException extends RuntimeException {

    private final int code;
    private final String rpcMessage;
    private final String rpcData;

    public RpcException(int code, String rpcMessage) {
        this(code, rpcMessage, null, null);
    }

    public RpcException(int code, String rpcMessage, String rpcData) {
        this(code, rpcMessage, rpcData, null);
    }

    public RpcException(int code, String rpcMessage, Throwable e) {
        this(code, rpcMessage, null, e);
    }

    public RpcException(int code, String rpcMessage, String rpcData, Throwable e) {
        super("RPC Exception " + code + ": " + rpcMessage + (rpcData == null ? "" : " (" + rpcData + ")"), e);
        this.code = code;
        this.rpcMessage = rpcMessage;
        this.rpcData = rpcData;
    }

    public int getCode() {
        return code;
    }

    public String getRpcMessage() {
        return rpcMessage;
    }

    public String getRpcData() {
        return rpcData;
    }
}
