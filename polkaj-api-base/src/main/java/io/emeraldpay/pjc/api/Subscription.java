package io.emeraldpay.pjc.api;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Subscription to a series of response ("events") from the server. Use {@link Subscription#handler(Consumer)} to start receiving
 * events. When the subscription is not needed call {@link Subscription#close()}
 *
 * @param <T> type of the expected data provided with each event
 */
public interface Subscription<T> extends AutoCloseable {

    /**
     * Add handler to the subscription. If added twice, a new handler replaces previous. Providing null removes handler.
     *
     * @param handler handler that consumer all new events
     */
    void handler(Consumer<? extends Subscription.Event<? extends T>> handler);

    /**
     * Unsubscribes from the current subscription (i.e., by calling the server with unsubscribe method).
     * @throws Exception if an unresolvable error happened
     */
    @Override
    void close() throws Exception;

    /**
     * Container for a new message for the current subscription
     * @param <T> type of the expected result
     */
    public static class Event<T> {
        private final String method;
        private final T result;

        public Event(String method, T result) {
            this.method = method;
            this.result = result;
        }

        /**
         * Subscription event provides a name of the method (ex <code>chain_newHead</code>) that supposed to reference data type.
         * The method is provided by the server, and may differ per event.
         * @return method name
         */
        public String getMethod() {
            return method;
        }

        /**
         * Data provided with the event
         * @return event data
         */
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
