package io.emeraldpay.pjc.api;

import com.fasterxml.jackson.databind.JavaType;

import java.util.List;

/**
 * Definition of a JSON RPC Call used to subscribe to a series of responses (i.e., via WebSockets).
 * <br>
 * For example method <code>chain_subscribeNewHead</code> provides subscription to new blocks (represented with <code>BlockJson.Header</code>)
 * And to cancel the subscription you need to execute <code>chain_unsubscribeNewHead</code>
 *
 * @param <R> type of the response data
 */
public class SubscribeCall<R> extends AbstractCall<R> {

    private final String unsubscribe;

    protected SubscribeCall(String method, String unsubscribe, Object[] params) {
        super(method, params);
        if (unsubscribe == null || unsubscribe.isEmpty()) {
            throw new IllegalArgumentException("Unsubscribe method cannot be null or empty");
        }
        this.unsubscribe = unsubscribe;
    }

    protected SubscribeCall(Class<?> resultClazz, String method, String unsubscribe, Object[] params) {
        this(method, unsubscribe, params);
        if (resultClazz == null) {
            throw new NullPointerException("Result Class is null");
        }
        this.resultClazz = resultClazz;
    }

    protected SubscribeCall(JavaType resultType, String method, String unsubscribe, Object[] params) {
        this(method, unsubscribe, params);
        if (resultType == null) {
            throw new NullPointerException("Result Type is null");
        }
        this.resultType = resultType;
    }

    /**
     * Creates a new command
     *
     * @param resultClazz expected result class
     * @param method method name
     * @param unsubscribe method to unsubscribe
     * @param params params to the method
     * @param <T> type of the result
     * @return command instance
     */
    public static <T> SubscribeCall<T> create(Class<T> resultClazz, String method, String unsubscribe, List<?> params) {
        return create(resultClazz, method, unsubscribe, params.toArray());
    }

    /**
     * Creates a new command
     *
     * @param resultClazz expected result class
     * @param method method name
     * @param unsubscribe method to unsubscribe
     * @param params params to the method
     * @param <T> type of the result
     * @return command instance
     */
    public static <T> SubscribeCall<T> create(Class<T> resultClazz, String method, String unsubscribe, Object... params) {
        return new SubscribeCall<T>(resultClazz, method, unsubscribe, params);
    }

    /**
     * Creates a new command
     *
     * @param resultType expected result type
     * @param method method name
     * @param unsubscribe method to unsubscribe
     * @param params params to the method
     * @param <T> type of the result
     * @return command instance
     */
    public static <T> SubscribeCall<T> create(JavaType resultType, String method, String unsubscribe, List<?> params) {
        return create(resultType, method, unsubscribe, params.toArray());
    }

    /**
     * Creates a new command
     *
     * @param resultType expected result type
     * @param method method name
     * @param unsubscribe method to unsubscribe
     * @param params params to the method
     * @param <T> type of the result
     * @return command instance
     */    public static <T> SubscribeCall<T> create(JavaType resultType, String method, String unsubscribe, Object... params) {
        return new SubscribeCall<T>(resultType, method, unsubscribe, params);
    }

    /**
     *
     * @return Method used to unsubscribe from the currently running subscription
     */
    public String getUnsubscribe() {
        return unsubscribe;
    }

    public <T> SubscribeCall<T> cast(Class<T> resultClazz) {
        return (SubscribeCall<T>) super.cast(resultClazz);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubscribeCall)) return false;
        if (!super.equals(o)) return false;
        SubscribeCall<?> that = (SubscribeCall<?>) o;
        return unsubscribe.equals(that.unsubscribe);
    }

    @Override
    public int hashCode() {
        // unsubscribe method is all correct situations would follow main method, so it's not necessary to use it as
        // a part of hashCode in addition to main method
        return super.hashCode();
    }
}
