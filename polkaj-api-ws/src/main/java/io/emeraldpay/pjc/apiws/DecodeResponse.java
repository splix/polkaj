package io.emeraldpay.pjc.apiws;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.emeraldpay.pjc.api.RpcResponse;
import io.emeraldpay.pjc.api.RpcResponseError;

import java.io.IOException;

/**
 * Decoded JSON response from WebSocket message. The message itself may be a JSON with Subscription event, or standard JSON RPC response.
 * The decoder verifies the content, and depending on it, returns WsResponse with correct type.
 *
 * @see WsResponse
 */
public class DecodeResponse {

    private final ObjectMapper objectMapper;
    private final TypeMapping subscriptionMapping;
    private final TypeMapping rpcMapping;

    public DecodeResponse(ObjectMapper objectMapper, TypeMapping rpcMapping, TypeMapping subscriptionMapping) {
        this.objectMapper = objectMapper;
        this.rpcMapping = rpcMapping;
        this.subscriptionMapping = subscriptionMapping;
    }

    public <T> WsResponse decode(String json) throws IOException {
        JsonFactory jsonFactory = objectMapper.getFactory();
        JsonParser parser = jsonFactory.createParser(json);
        if (parser.nextToken() != JsonToken.START_OBJECT) {
            throw new IllegalStateException("Not an object");
        }
        String method = null;
        WsResponse.IdValue value = null;
        Preparsed preparsed = new Preparsed(objectMapper);
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            if (parser.currentToken() == null) {
                throw new IllegalStateException("JSON finished before data received");
            }
            if (parser.currentToken() != JsonToken.FIELD_NAME) {
                continue;
            }
            String field = parser.currentName();
            if ("jsonrpc".equals(field)) {
                //just skip it
            } else if ("id".equals(field)) {
                preparsed.id = decodeNumber(parser);
                preparsed.type = findType(rpcMapping, preparsed.id);
                if (preparsed.isReady()) {
                    var result = preparsed.build();
                    return WsResponse.rpc(new RpcResponse<>(result.getId(), result.getValue()));
                }
            } else if ("result".equals(field)) {
                parser.nextToken();
                preparsed.node = parser.readValueAsTree();
                if (preparsed.isReady()) {
                    var result = preparsed.build();
                    return WsResponse.rpc(new RpcResponse<>(result.getId(), result.getValue()));
                }
            } else if ("error".equals(field)) {
                //TODO parse error
                preparsed.error = decodeError(parser);
                if (preparsed.id != null) {
                    var result = preparsed.build();
                    return WsResponse.rpc(new RpcResponse<>(result.getId(), result.getValue()));
                }
            } else if ("method".equals(field)) {
                parser.nextToken();
                method = parser.getValueAsString();
                if (value != null) {
                    return WsResponse.subscription(
                            new PolkadotWsApi.SubscriptionResponse<>(value.getId(), method, value.getValue())
                    );
                }
            } else if ("params".equals(field)) {
                value = decodeSubscription(subscriptionMapping, parser);
                if (method != null) {
                    return WsResponse.subscription(
                            new PolkadotWsApi.SubscriptionResponse<>(value.getId(), method, value.getValue())
                    );
                }
            }
        }
        throw new IllegalStateException("Either id or result not found in JSON");
    }

    protected RpcResponseError decodeError(JsonParser parser) throws IOException {
        int code = 0;
        String message = null;
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            if (parser.currentToken() == null) {
                break;
            }
            String field = parser.currentName();
            if ("code".equals(field)) {
                code = decodeNumber(parser);
            } else if ("message".equals(field)) {
                message = parser.getValueAsString();
            }
        }
        return new RpcResponseError(code, message);

    }

    protected WsResponse.IdValue decodeSubscription(TypeMapping typeMapping, JsonParser parser) throws IOException {
        Preparsed preparsed = new Preparsed(objectMapper);
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            if (parser.currentToken() == null) {
                throw new IllegalStateException("JSON finished before data received");
            }

            String field = parser.currentName();
            if ("subscription".equals(field)) {
                preparsed.id = decodeNumber(parser);
                preparsed.type = findType(typeMapping, preparsed.id);
                if (preparsed.isReady()) {
                    return preparsed.build();
                }
            } else if ("result".equals(field)) {
                parser.nextToken();
                preparsed.node = parser.readValueAsTree();
                if (preparsed.isReady()) {
                    return preparsed.build();
                }
                parser.nextToken();
            }
        }
        throw new IllegalStateException("Either id or result not found in JSON");
    }

    private JavaType findType(TypeMapping typeMapping, Integer id) {
        JavaType type;
        type = typeMapping.get(id);
        if (type == null) {
            throw new IllegalStateException("Unknown request: " + id);
        }
        return type;
    }

    private Integer decodeNumber(JsonParser parser) throws IOException {
        if (parser.currentToken() != JsonToken.VALUE_NUMBER_INT) {
            parser.nextToken();
        }
        if (!parser.currentToken().isNumeric()) {
            throw new IllegalStateException("Id is not a number");
        }
        return parser.getIntValue();
    }

    public interface TypeMapping {
        JavaType get(int id);
    }

    private static class Preparsed {
        private final ObjectMapper objectMapper;

        Integer id = null;
        JavaType type = null;
        TreeNode node = null;

        RpcResponseError error;

        public Preparsed(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        public boolean isReady() {
            return id != null &&
                    error != null || (type != null && node != null);
        }

        public WsResponse.IdValue build() throws IOException {
            if (id == null) {
                throw new IllegalStateException("Id is not set");
            }
            if (error != null) {
                return new WsResponse.IdValue(id, error);
            }
            if (type == null) {
                throw new IllegalStateException("Type is not set");
            }
            if (node != null) {
                Object value = objectMapper
                        .readerFor(type)
                        .readValue(node.traverse(objectMapper));
                return new WsResponse.IdValue(id, value);
            }
            throw new IllegalStateException("Not ready");
        }
    }

}
