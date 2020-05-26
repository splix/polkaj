package io.emeraldpay.pjc.api;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.Arrays;
import java.util.Objects;

public abstract class AbstractCall<R> {

    protected final String method;
    protected final Object[] params;
    protected Class<?> resultClazz;
    protected JavaType resultType;

    protected AbstractCall(String method, Object[] params) {
        if (method == null || method.isEmpty()) {
            throw new IllegalArgumentException("Method cannot be null or empty");
        }
        this.method = method;
        if (params == null) {
            params = new Object[0];
        }
        this.params = params;
    }

    /**
     *
     * @return RPC methods name to execute
     */
    public String getMethod() {
        return method;
    }

    /**
     *
     * @param typeFactory type factory to deduce Jackson JavaType from Java Class. Optional, required only if only a Class was set.
     * @return type of the result to map from JSON to Java
     * @see TypeFactory
     */
    public JavaType getResultType(TypeFactory typeFactory) {
        if (resultType != null) {
            return resultType;
        }
        if (typeFactory == null) {
            throw new NullPointerException("TypeFactory is required when Result Type is set as a Class");
        }
        return typeFactory.constructType(resultClazz);
    }

    /**
     *
     * @return list of RPC params, can be empty
     */
    public Object[] getParams() {
        return params;
    }

    @SuppressWarnings("unchecked")
    public <T> AbstractCall<T> cast(Class<T> resultClazz) {
        if (resultType != null) {
            if (resultType.isTypeOrSubTypeOf(resultClazz)) {
                return (AbstractCall<T>) this;
            }
            throw new ClassCastException("Cannot cast " + this.resultType.getRawClass() + " to " + resultClazz);
        }
        if (resultClazz.isAssignableFrom(this.resultClazz)) {
            return (AbstractCall<T>) this;
        }
        throw new ClassCastException("Cannot cast " + this.resultClazz + " to " + resultClazz);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractCall)) return false;
        AbstractCall<?> rpcCall = (AbstractCall<?>) o;
        return method.equals(rpcCall.method) &&
                Arrays.equals(params, rpcCall.params) &&
                Objects.equals(resultClazz, rpcCall.resultClazz) &&
                Objects.equals(resultType, rpcCall.resultType);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(method);
        result = 31 * result + Arrays.hashCode(params);
        return result;
    }
}
