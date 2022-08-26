package io.emeraldpay.polkaj.api.internal;

import java.util.Objects;

public class SubscriptionResponse<T> {
    private final String id;
    private final String method;
    private final T value;

    public SubscriptionResponse(String id, String method, T value) {
        this.id = id;
        this.method = method;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getMethod() {
        return method;
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubscriptionResponse)) return false;
        SubscriptionResponse<?> that = (SubscriptionResponse<?>) o;
        return id.equals(that.id) &&
                Objects.equals(method, that.method) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, method, value);
    }
}
