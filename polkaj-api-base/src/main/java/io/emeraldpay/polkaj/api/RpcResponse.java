package io.emeraldpay.polkaj.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;

@JsonDeserialize
@JsonIgnoreProperties("jsonrpc")
public class RpcResponse<T> {
    private int id;
    private T result;
    private RpcResponseError error;

    public RpcResponse() {
    }

    public RpcResponse(int id, T result) {
        this.id = id;
        // result can be passed as an Object instance, so we need to make sure it's not Object of Error
        if (result instanceof RpcResponseError) {
            this.error = (RpcResponseError) result;
        } else {
            this.result = result;
        }
    }

    public RpcResponse(int id, RpcResponseError error) {
        this.id = id;
        this.error = error;
    }

    public int getId() {
        return id;
    }

    public T getResult() {
        return result;
    }

    public RpcResponseError getError() {
        return error;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RpcResponse)) return false;
        RpcResponse<?> that = (RpcResponse<?>) o;
        return id == that.id &&
                Objects.equals(result, that.result) &&
                Objects.equals(error, that.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, result, error);
    }
}