package io.emeraldpay.pjc.client;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Arrays;
import java.util.Objects;

@JsonSerialize
public class RpcRequest {
    private final String jsonrpc = "2.0";
    private final int id;
    private final String method;
    private final Object[] params;

    public RpcRequest(int id, String method, Object[] params) {
        this.id = id;
        this.method = method;
        this.params = params;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public int getId() {
        return id;
    }

    public String getMethod() {
        return method;
    }

    public Object[] getParams() {
        return params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RpcRequest)) return false;
        RpcRequest that = (RpcRequest) o;
        return id == that.id &&
                Objects.equals(method, that.method) &&
                Arrays.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, method);
        result = 31 * result + Arrays.hashCode(params);
        return result;
    }
}
