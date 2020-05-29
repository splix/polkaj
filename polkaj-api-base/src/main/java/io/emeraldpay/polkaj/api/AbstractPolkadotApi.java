package io.emeraldpay.polkaj.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractPolkadotApi implements PolkadotApi {

    protected final AtomicInteger id = new AtomicInteger(0);
    protected final ObjectMapper objectMapper;

    public AbstractPolkadotApi(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JavaType responseType(JavaType resultType) {
        return objectMapper.getTypeFactory().constructType(resultType);
    }

    public JavaType responseType(Class<?> resultClazz) {
        return objectMapper.getTypeFactory().constructType(resultClazz);
    }

    public int nextId() {
        return id.getAndIncrement();
    }

    /**
     * Decode JSON RPC response
     *
     * @param id expected id
     * @param content full JSON content
     * @param clazz expected JavaType for the result field
     * @param <T> returning type
     * @return The decoded result
     * @throws CompletionException with RpcException details to let executor know that the response is invalid
     */
    public <T> T decode(int id, String content, JavaType clazz) {
        JavaType type = objectMapper.getTypeFactory().constructParametricType(RpcResponse.class, clazz);
        RpcResponse<T> response;
        try {
            response = objectMapper.readerFor(type).readValue(content);
        } catch (JsonProcessingException e) {
            throw new CompletionException(
                    new RpcException(-32603, "Server returned invalid JSON", e)
            );
        }
        if (id != response.getId()) {
            throw new CompletionException(
                    new RpcException(-32603, "Server returned invalid id: " + id + " != " + response.getId())
            );
        }
        if (response.getError() != null) {
            throw new CompletionException(
                    new RpcException(response.getError().getCode(), response.getError().getMessage())
            );
        }
        return response.getResult();
    }

    /**
     * Encode RPC request as JSON
     *
     * @param id id of the request
     * @param method method name
     * @param params params
     * @return full JSON of the request
     * @throws JsonProcessingException if cannod encode some of the params into JSON
     */
    public byte[] encode(int id, String method, Object[] params) throws JsonProcessingException {
        RpcRequest request = new RpcRequest(id, method, params);
        return objectMapper.writeValueAsBytes(request);
    }
}
