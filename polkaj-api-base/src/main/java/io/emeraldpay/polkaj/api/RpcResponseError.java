package io.emeraldpay.polkaj.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;

@JsonDeserialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class RpcResponseError {
    private int code;
    private String message;
    private String data;

    public RpcResponseError() {
    }

    public RpcResponseError(int code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RpcResponseError)) return false;
        RpcResponseError that = (RpcResponseError) o;
        return code == that.code &&
                Objects.equals(message, that.message) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, data);
    }
}