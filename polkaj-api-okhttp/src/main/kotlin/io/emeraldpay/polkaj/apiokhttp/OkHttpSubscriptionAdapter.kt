package io.emeraldpay.polkaj.apiokhttp

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.emeraldpay.polkaj.api.*
import io.emeraldpay.polkaj.api.internal.DecodeResponse
import io.emeraldpay.polkaj.api.internal.WsResponse
import io.emeraldpay.polkaj.apiokhttp.Constants.APPLICATION_JSON
import io.emeraldpay.polkaj.json.jackson.PolkadotModule
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.future.asCompletableFuture
import okhttp3.*
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

/**
 * OkHttp Websocket based client to Polkadot API. In addition to standard RPC calls it supports subscription to events, i.e.
 * when a call provides multiple responses.
 */
class OkHttpSubscriptionAdapter private constructor(
    private val target : String,
    private val client : OkHttpClient,
    private val scope : CoroutineScope,
    private val rpcCoder: RpcCoder,
    private val onClose : () -> Unit
) : SubscriptionAdapter {

    companion object{
        @JvmStatic
        fun newBuilder() : Builder = Builder()
    }

    private data class State(
        val socketState : SocketState = SocketState.Idle,
        val rpcCalls : List<RpcDeferred<*>> = emptyList(),
        val subscriptionCalls : List<FlowSubscription<*>> = emptyList(),
        val curSocketJob : Job? = null,
        val startingSub : Set<RpcCall<*>> = setOf()
    )

    private sealed class SocketState {
        object Idle : SocketState()
        object Connecting : SocketState()
        data class Connected(val webSocket: WebSocket) : SocketState()
        object Closing : SocketState()
        object Closed : SocketState()
        data class Failed(val throwable: Throwable?) : SocketState()
    }

    private data class RpcDeferred<T>(
        val id : Int,
        val call: RpcCall<out T>,
        val deferred: CompletableDeferred<out T>
    )

    private class WebscoketFailedException(cause : Throwable?) : Exception(cause)

    @Suppress("BlockingMethodInNonBlockingContext")
    private class FlowSubscription<T>(
        val id : String,
        val call: SubscribeCall<T>,
        private val scope: CoroutineScope,
        private val events : Flow<Subscription.Event<T>>,
        private val onClose: (FlowSubscription<T>) -> Unit) : Subscription<T> {

        private var job : Job? = null

        override fun handler(handler: Consumer<out Subscription.Event<out T>>?) {
            job?.cancel()
            if(handler == null) return
            @Suppress("UNCHECKED_CAST")
            val consumer = handler as Consumer<Subscription.Event<out T>>
            job = events.onEach {
                consumer.accept(it)
            }.launchIn(scope)
        }

        override fun close() {
            job?.cancel()
            onClose(this)
        }
    }

    private val _state = MutableStateFlow(State())
    private val _messages = MutableSharedFlow<WsResponse>(0)

    private val curState = _state.asStateFlow()
    private val messages = _messages.asSharedFlow()
    private val rpcEvents = messages.filter { it.type == WsResponse.Type.RPC }.map { it.asRpc() }
    private val subscriptionEvents = messages.filter { it.type == WsResponse.Type.SUBSCRIPTION }.map { it.asEvent() }
    private val decodeResponse : DecodeResponse

    init {
        val rpcMapping = { id : Int ->
            curState.value.rpcCalls.firstOrNull { it.id == id }?.call?.getResultType(rpcCoder.objectMapper.typeFactory)
        }
        val subMapping = { id : String ->
            runBlocking {
                curState.transformWhile { state ->
                    state.subscriptionCalls.firstOrNull{ it.id == id}?.call?.getResultType(rpcCoder.objectMapper.typeFactory)?.let {
                        emit(it)
                    }
                    state.startingSub.isNotEmpty()
                }.first()
            }

        }
        decodeResponse = DecodeResponse(rpcCoder.objectMapper, rpcMapping, subMapping)
    }

    override fun close() {
        curState.value.socketState.let {
            if(it is SocketState.Connected) it.webSocket.close(1000, "close")
        }
        onClose()
    }

    @Suppress("UNCHECKED_CAST", "BlockingMethodInNonBlockingContext")
    override fun <T : Any?> produceRpcFuture(call: RpcCall<T>): CompletableFuture<T> {
        val result = CompletableDeferred<T>()
        val exHandler = CoroutineExceptionHandler { _, throwable ->
            result.completeExceptionally(throwable)
        }
        scope.launch(Dispatchers.IO + exHandler) {
            val id = rpcCoder.nextId()
            val payload = try {
                rpcCoder.encode(id, call)
            } catch (e : JsonProcessingException){
                result.completeExceptionally(e)
                null
            }
            if(payload != null) {
                val rpc = RpcDeferred(id, call, result)
                _state.update {
                    it.copy(rpcCalls = it.rpcCalls + rpc)
                }
                curState.socket().send(String(payload))
                rpcEvents.first { it.id == id }.let {
                    if(it.error != null) result.completeExceptionally(RpcException(it.error.code, it.error.message, it.error.data))
                    else result.complete(it.result as T)
                }.also {
                    _state.update {
                        it.copy(rpcCalls = it.rpcCalls - rpc)
                    }
                }
            }
        }

        return result.asCompletableFuture()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> subscribe(call: SubscribeCall<T>): CompletableFuture<Subscription<T>> {
        val start = RpcCall.create(String::class.java, call.method, *call.params)
        _state.update { it.copy(startingSub = it.startingSub + start) }
        return produceRpcFuture(start).thenApply { id ->
            val events = subscriptionEvents.filter { it.id ==  id }.map { Subscription.Event(it.method, it.value as T) }
            FlowSubscription(id, call, scope, events) { sub ->
                produceRpcFuture(RpcCall.create(Boolean::class.java, call.unsubscribe, id))
                _state.update {
                    it.copy(subscriptionCalls = it.subscriptionCalls - sub)
                }
            }.also { sub ->
                _state.update {
                    it.copy(subscriptionCalls = it.subscriptionCalls + sub, startingSub = it.startingSub - start)
                }
            }
        }
    }

    private fun createSocket() : Flow<SocketState> {
        val request = Request.Builder().apply {
            url(target)
            header("User-Agent", "PolkaJ/OkHttp/0.5")
            header("Content-Type", APPLICATION_JSON)
        }.build()

        return callbackFlow {
            trySend(SocketState.Connecting)
            val webSocket = client.newWebSocket(request, object : WebSocketListener(){
                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    trySend(SocketState.Closed)
                    close()
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    trySend(SocketState.Closing)
                    close()
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    t.printStackTrace(System.err)
                    trySend(SocketState.Failed(t))
                    close()
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    val handler = CoroutineExceptionHandler{ _, throwable ->
                        throwable.printStackTrace(System.err)
                    }
                    @Suppress("BlockingMethodInNonBlockingContext")
                    scope.launch(Dispatchers.IO + handler) { _messages.emit(decodeResponse.decode(text)) }
                }

                override fun onOpen(webSocket: WebSocket, response: Response) {
                    trySend(SocketState.Connected(webSocket))
                }
            })

            awaitClose {
                if(curState.value.socketState == SocketState.Connecting) webSocket.cancel()
            }

        }.buffer(Channel.UNLIMITED).onEach {
                when(it){
                    is SocketState.Connected -> _state += it
                    SocketState.Connecting -> _state += it
                    is SocketState.Failed -> handleSocketException(it.throwable)
                    else -> _state += SocketState.Idle
                }
        }
    }

    private fun createSocketIfNeeded() = _state.update {
        when(it.socketState){
            is SocketState.Connected, SocketState.Connecting -> { it }
            else -> {
                it.curSocketJob?.cancel()
                val newJob = createSocket().catch { error ->
                    handleSocketException(error)
                }.launchIn(scope)
                it.copy(curSocketJob = newJob)
            }
        }
    }

    private fun handleSocketException(t : Throwable?){
        val exception = WebscoketFailedException(t)
        _state.update { state ->
            state.rpcCalls.forEach { call-> call.deferred.completeExceptionally(exception) }
            //TODO update subscription handler to take error events and report error here
            state.copy(socketState = SocketState.Idle, rpcCalls = emptyList(), subscriptionCalls = emptyList())
        }
    }

    private suspend fun StateFlow<State>.socket() : WebSocket = also {
        createSocketIfNeeded()
    }.map {
        it.socketState
    }.transform {
        if(it is SocketState.Connected) emit(it.webSocket)
    }.first()

    private operator fun MutableStateFlow<State>.plusAssign(socketState: SocketState) {
        _state.update {
            it.copy(socketState = socketState)
        }
    }

     class Builder private constructor(
        private var target : String = "ws://127.0.0.1:9944",
        private var client : OkHttpClient = OkHttpClient.Builder().apply {
            callTimeout(Duration.ofMinutes(1))
            followRedirects(false)
        }.build(),
        private var scope : CoroutineScope = CoroutineScope(SupervisorJob()),
        private var rpcCoder: RpcCoder = RpcCoder(ObjectMapper().registerModule(PolkadotModule())),
        private var onClose : () -> Unit = {
            scope.cancel()
            client.dispatcher.executorService.shutdown()
        }
    ){
        companion object{
            operator fun invoke(block : Builder.() -> Unit) : OkHttpSubscriptionAdapter {
                return Builder().apply { block() }.build()
            }

            operator fun invoke() : Builder {
                return Builder()
            }
        }

        /**
        * Server address URL.
        * By default, it will be set to "ws://127.0.0.1:9944"
        *
        * @param target URL
        * @return builder
        */
        fun connectTo(target: String) = apply { this.target = target }

        /**
        * Provide a custom OkHttpClient configured
        *
        * @param client OkHttpClient
        * @return builder
        */
        fun client(client : OkHttpClient) = apply { this.client = client }

        /**
         * CoroutineScope for requests and subscription.
         * By default, a new Scope will be created.
        */
        fun scope(scope : CoroutineScope) = apply { this.scope = scope }

        /**
        * Provide a custom RpcCoder for rpc serialization.
        *
        * @param rpcCoder rpcCoder
        * @return builder
        */
        fun rpcCoder(rpcCoder : RpcCoder) = apply { this.rpcCoder = rpcCoder }

        /**
        * Provide custom cleanup method.
        * By default, it will cancel the [scope] and shutdown the [client] executorService
        *
        * @param block to be called on close.
        * @return builder
        */
        fun onClose(block : () -> Unit ) = apply { onClose = block }

         /**
         * Provide a custom timeout
       * By default, it is 1 minute.
       */
         fun timeout(timeout : Duration) = apply { client = client.newBuilder().callTimeout(timeout).build() }

         /**
          * Apply configuration and build a new adapter
          * @return new instance of [OkHttpSubscriptionAdapter]
          */
         fun build() : OkHttpSubscriptionAdapter = OkHttpSubscriptionAdapter(target, client, scope, rpcCoder, onClose)
    }
}