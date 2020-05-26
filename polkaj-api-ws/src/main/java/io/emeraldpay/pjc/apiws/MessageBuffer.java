package io.emeraldpay.pjc.apiws;

import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Buffer for WebSocket messages, when messages come split into several frames
 */
public class MessageBuffer {

    private final ConcurrentHashMap<WebSocket, List<CharSequence>> buffer = new ConcurrentHashMap<>();

    /**
     * Add non-last message to the buffer
     *
     * @param source source connection
     * @param part message part
     */
    void add(WebSocket source, CharSequence part) {
        List<CharSequence> current = buffer.computeIfAbsent(source, (ws) -> new ArrayList<>());
        current.add(part);
    }

    /**
     * Provide the last message. Finalizes the buffer, if exists, and returns concatenated result.
     *
     * @param source source connection
     * @param part message part
     * @return resulting message
     */
    String last(WebSocket source, CharSequence part) {
        if (!buffer.containsKey(source)) {
            return part.toString();
        } else {
            List<CharSequence> current = buffer.remove(source);
            if (current == null || current.isEmpty()) {
                // should never happen, it either contains value in buffer, or doesn't exist at all
                return part.toString();
            } else {
                current.add(part);
                return String.join("", current);
            }
        }
    }

}
