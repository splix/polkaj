package io.emeraldpay.pjc.apiws;

import com.fasterxml.jackson.databind.JavaType;

import java.util.Objects;
import java.util.function.Consumer;

public class Subscription<T> implements AutoCloseable, Consumer<Subscription.Event<T>> {

    private Integer id;
    private final JavaType type;
    private final String unsubscribeMethod;
    private final PolkadotWsApi client;
    private Consumer<Event<T>> handler;

    public Subscription(JavaType type, String unsubscribeMethod, PolkadotWsApi client) {
        this.type = type;
        this.unsubscribeMethod = unsubscribeMethod;
        this.client = client;
    }

    public Integer getId() {
        return id;
    }

    public JavaType getType() {
        return type;
    }

    public void setId(int id) {
        if (this.id != null) {
            throw new IllegalStateException("Subscription id is already set to " + this.id);
        }
        this.id = id;
    }

    public void handler(Consumer<Event<T>> handler) {
        this.handler = handler;
    }

    public void accept(Event<T> event) {
        Consumer<Event<T>> handler = this.handler;
        if (handler == null) {
            return;
        }
        handler.accept(event);
    }

    @Override
    public void close() throws Exception {
        if (id == null) {
            return;
        }
        client.execute(Boolean.class, unsubscribeMethod, id);
        client.removeSubscription(id);
    }

    public static class Event<T> {
        private final String method;
        private final T result;

        public Event(String method, T result) {
            this.method = method;
            this.result = result;
        }

        public String getMethod() {
            return method;
        }

        public T getResult() {
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Event)) return false;
            Event<?> event = (Event<?>) o;
            return Objects.equals(method, event.method) &&
                    Objects.equals(result, event.result);
        }

        @Override
        public int hashCode() {
            return Objects.hash(method, result);
        }
    }
}
