package io.emeraldpay.pjc.api;

import com.fasterxml.jackson.databind.JavaType;

import java.util.List;

/**
 * Definition of a JSON RPC call.
 *
 * @param <R> Type of the result
 */
public class RpcCall<R> extends AbstractCall<R> {

    private RpcCall(String method, Object[] params) {
        super(method, params);
    }

    protected RpcCall(Class<?> resultClazz, String method, Object[] params) {
        this(method, params);
        if (resultClazz == null) {
            throw new NullPointerException("Result Class is null");
        }
        this.resultClazz = resultClazz;
        this.resultType = null;
    }

    protected RpcCall(JavaType resultType, String method, Object[] params) {
        this(method, params);
        if (resultType == null) {
            throw new NullPointerException("Result JavaType is null");
        }
        this.resultClazz = null;
        this.resultType = resultType;
    }

    /**
     * Creates a new command
     *
     * @param resultClazz expected resulting class
     * @param method method name
     * @param params params to the method
     * @param <T> type of the result
     * @return command instance
     */
    public static <T> RpcCall<T> create(Class<T> resultClazz, String method, List<?> params) {
        return create(resultClazz, method, params.toArray());
    }

    /**
     * Creates a new command
     *
     * @param resultClazz expected resulting class
     * @param method method name
     * @param params params to the method
     * @param <T> type of the result
     * @return command instance
     */
    public static <T> RpcCall<T> create(Class<T> resultClazz, String method, Object... params) {
        return new RpcCall<>(resultClazz, method, params);
    }

    /**
     * Creates a new command
     *
     * @param resultType expected result type
     * @param method method name
     * @param params params to the method
     * @param <T> type of the result
     * @return command instance
     */
    public static <T> RpcCall<T> create(JavaType resultType, String method, List<?> params) {
        return create(resultType, method, params.toArray());
    }

    /**
     * Creates a new command
     *
     * @param resultType expected result type
     * @param method method name
     * @param params params to the method
     * @param <T> type of the result
     * @return command instance
     */
    public static <T> RpcCall<T> create(JavaType resultType, String method, Object... params) {
        return new RpcCall<>(resultType, method, params);
    }

    public <T> RpcCall<T> cast(Class<T> resultClazz) {
        return (RpcCall<T>) super.cast(resultClazz);
    }

    @SuppressWarnings("unchecked")
    public RpcCall<List<R>> expectList() {
        super.expectList();
        return (RpcCall<List<R>>) this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RpcCall)) return false;

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
