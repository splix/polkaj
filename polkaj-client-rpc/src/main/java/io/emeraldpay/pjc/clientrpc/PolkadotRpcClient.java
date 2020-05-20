package io.emeraldpay.pjc.clientrpc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.emeraldpay.pjc.json.jackson.PolkadotModule;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Default JSON RPC HTTP client for Polkadot API. It uses Java 11 HttpClient implementation for requests.
 * Each request made from that client has a uniq id, from a monotone sequence starting on 0. A new instance is
 * supposed to be create through <code>PolkadotRpcClient.newBuilder()</code>:
 * <br>
 * The class is AutoCloseable, with <code>.close()</code> methods, which shutdown a thread (or threads) used for http requests.
 *
 * <br>
 * Example:
 * <pre><code>
 * PolkadotRpcClient client = PolkadotRpcClient.newBuilder().build();
 * Future&lt;Hash256&gt; hash = client.execute(Hash256.class, "chain_getFinalisedHead");
 * System.out.println("Current head: " + hash.get());
 * </code></pre>
 */
public class PolkadotRpcClient implements AutoCloseable {

    private static final String APPLICATION_JSON = "application/json";

    private final HttpClient httpClient;
    private final Runnable onClose;
    private final AtomicInteger id = new AtomicInteger(0);
    private final ObjectMapper objectMapper;
    private final HttpRequest.Builder request;
    private boolean closed = false;

    private PolkadotRpcClient(URI target, HttpClient httpClient, String basicAuth, Runnable onClose, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.onClose = onClose;
        this.objectMapper = objectMapper;

        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(target)
                .timeout(Duration.ofMinutes(1))
                .header("User-Agent", "Polkaj/0.3") //TODO generate version during compilation
                .header("Content-Type", APPLICATION_JSON);

        if (basicAuth != null) {
            request = request.header("Authorization", basicAuth);
        }

        this.request = request;
    }

    public JavaType responseType(Class clazz) {
        return objectMapper.getTypeFactory().constructType(clazz);
    }

    /**
     * Execute JSON RPC request
     *
     * @param clazz expected resulting class, i.e. will be used by Jackson to parse JSON value of <code>result</code> field
     * @param method method name
     * @param params params to the method
     * @param <T> type of the result
     * @return CompletableFuture for the result. Note that the Future may throw RpcException when it get
     * @see RpcException
     */
    public <T> CompletableFuture<T> execute(Class<T> clazz, String method, Object... params) {
        if (closed) {
            return CompletableFuture.failedFuture(
                    new IllegalStateException("Client is already closed")
            );
        }
        int id = this.id.getAndIncrement();
        JavaType type = responseType(clazz);
        try {
            HttpRequest.Builder request = this.request.copy()
                    .POST(HttpRequest.BodyPublishers.ofByteArray(encode(id, method, params)));
            return httpClient.sendAsync(request.build(), HttpResponse.BodyHandlers.ofString())
                    .thenApply(this::verify)
                    .thenApply(HttpResponse::body)
                    .thenApply(content -> decode(id, content, type));
        } catch (JsonProcessingException e) {
            return CompletableFuture.failedFuture(
                    new RpcException(-32600, "Unable to encode request as JSON: " + e.getMessage(), e)
            );
        }
    }

    /**
     * Verify the HTTP response meta, i.e. statuc code, headers, etc.
     *
     * @param response HTTP response from server
     * @return The response itself if all is ok
     * @throws CompletionException with RpcException details to let executor know that the response is invalid
     * @see CompletionException
     * @see RpcException
     */
    public HttpResponse<String> verify(HttpResponse<String> response) {
        if (response.statusCode() != 200) {
            throw new CompletionException(
                    new RpcException(-32000, "Server returned error status: " + response.statusCode())
            );
        }
        if (!APPLICATION_JSON.equals(response.headers().firstValue("content-type").orElse(APPLICATION_JSON))) {
            throw new CompletionException(
                    new RpcException(-32000, "Server returned invalid content-type: " + response.headers().firstValue("content-type"))
            );
        }
        return response;
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
                    new RpcException(-32603, "Server returned invalid id: " + id + " != " + response.id)
            );
        }
        if (response.error != null) {
            throw new CompletionException(
                    new RpcException(response.error.code, response.error.message)
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

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public void close() throws Exception {
        if (closed) {
            return;
        }
        if (onClose != null) {
            onClose.run();
        }
        closed = true;
    }

    @JsonSerialize
    private static class RpcRequest {
        private final String jsonrpc = "2.0";
        private final int id;
        private final String method;
        private final Object[] params;

        public RpcRequest(int id, String method, Object[] params) {
            this.id = id;
            this.method = method;
            this.params = params;
        }

        public String getJsonrpc() {
            return jsonrpc;
        }

        public int getId() {
            return id;
        }

        public String getMethod() {
            return method;
        }

        public Object[] getParams() {
            return params;
        }
    }

    @JsonDeserialize
    @JsonIgnoreProperties("jsonrpc")
    private static class RpcResponse<T> {
        private int id;
        private T result;
        private RpcResponseError error;

        public int getId() {
            return id;
        }

        public T getResult() {
            return result;
        }

        public RpcResponseError getError() {
            return error;
        }
    }

    @JsonDeserialize
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class RpcResponseError {
        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * PolkadotRpcClient builder. All of the configurations are optional, and the default build would use
     * a standard Java HttpClient without any authorization connecting to localhost:9933 and using
     * a new instance of a Jackson ObjectMapper with PolkadotModule enabled.
     *
     * @see PolkadotRpcClient
     * @see HttpClient
     * @see PolkadotModule
     */
    public static class Builder {
        private URI target;
        private ExecutorService executorService;
        private String basicAuth;
        private HttpClient httpClient;
        private Runnable onClose;
        private ObjectMapper objectMapper;


        /**
         * Setup Basic Auth for RPC calls
         *
         * @param username username
         * @param password password
         * @return builder
         */
        public Builder basicAuth(String username, String password) {
            this.basicAuth = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
            return this;
        }

        /**
         * Server address URL
         *
         * @param target URL
         * @return builder
         * @throws URISyntaxException if specified url is invalid
         */
        public Builder connectTo(String target) throws URISyntaxException {
            return this.connectTo(new URI(target));
        }

        /**
         * Server address URL
         *
         * @param target URL
         * @return builder
         */
        public Builder connectTo(URI target) {
            this.httpClient = null;
            this.target = target;
            return this;
        }

        /**
         * Provide a custom HttpClient configured
         *
         * @param httpClient client
         * @return builder
         */
        public Builder httpClient(HttpClient httpClient) {
            if (this.executorService != null) {
                throw new IllegalStateException("Custom HttpClient cannot be used with separate Executor");
            }
            this.httpClient = httpClient;
            return this;
        }

        /**
         * Provide a custom ExecutorService to run http calls by default HttpClient
         *
         * @param executorService executor
         * @return builder
         */
        public Builder executor(ExecutorService executorService) {
            if (this.httpClient != null) {
                throw new IllegalStateException("Custom HttpClient cannot be used with separate Executor");
            }
            this.executorService = executorService;
            if (this.onClose != null) {
                this.onClose.run();
            }
            this.onClose = null;
            return this;
        }

        /**
         * Provide a custom ObjectMapper that will be used to encode/decode request and responses.
         *
         * @param objectMapper ObjectMapper
         * @return builder
         */
        public Builder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        protected void initDefaults() {
            if (httpClient == null && target == null) {
                try {
                    connectTo("http://127.0.0.1:9933");
                } catch (URISyntaxException e) { }
            }
            if (executorService == null) {
                ExecutorService executorService = Executors.newCachedThreadPool();
                this.executorService = executorService;
                onClose = executorService::shutdown;
            }
            if (objectMapper == null) {
                objectMapper = new ObjectMapper();
                objectMapper.registerModule(new PolkadotModule());
            }
        }

        /**
         * Apply configuration and build client
         *
         * @return new instance of PolkadotRpcClient
         */
        public PolkadotRpcClient build() {
            initDefaults();

            if (this.httpClient == null) {
                httpClient = HttpClient.newBuilder()
                        .version(HttpClient.Version.HTTP_2)
                        .executor(executorService)
                        .followRedirects(HttpClient.Redirect.NEVER)
                        .build();
            }

            return new PolkadotRpcClient(target, httpClient, basicAuth, onClose, objectMapper);
        }

    }
}
