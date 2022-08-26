package io.emeraldpay.polkaj.apiokhttp

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.emeraldpay.polkaj.api.RpcCall
import io.emeraldpay.polkaj.api.RpcCallAdapter
import io.emeraldpay.polkaj.api.RpcCoder
import io.emeraldpay.polkaj.api.RpcException
import io.emeraldpay.polkaj.apiokhttp.Constants.APPLICATION_JSON
import io.emeraldpay.polkaj.json.jackson.PolkadotModule
import kotlinx.coroutines.*
import kotlinx.coroutines.future.future
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.lang.IllegalStateException
import java.time.Duration
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OkHttpRpcAdapter private constructor(
    private val target : HttpUrl,
    private val basicAuth : String?,
    private val client : OkHttpClient,
    private val scope : CoroutineScope,
    private val rpcCoder: RpcCoder,
    private val onClose : () -> Unit
) : RpcCallAdapter {

    companion object{
        @JvmStatic
        fun newBuilder() : Builder = Builder()
    }

    private var closed = false
    private val baseRequest : Request = Request.Builder().apply {
        url(target)
        header("User-Agent", "PolkaJ/OkHttp/0.5")
        header("Content-Type", APPLICATION_JSON)
        if(basicAuth != null) header("Authorization", basicAuth)
    }.build()

    override fun close() {
        if(closed) return
        closed = true
        try{
            onClose()
        }catch (t : Throwable){
            System.err.println("Error during onClose call: ${t.message}")
        }
    }

    override fun <T : Any?> produceRpcFuture(call: RpcCall<T>): CompletableFuture<T> {
        return if(closed){
            CompletableFuture<T>().apply {
                completeExceptionally(IllegalStateException("Client is already closed"))
            }
        } else{
            scope.future{
                await(call)
            }
        }
    }

    fun nextId() : Int = rpcCoder.nextId()

    fun <T> getCall(id: Int, rpcCall: RpcCall<T>) : Call {
        return baseRequest.newBuilder().post(
            rpcCoder.encode(id, rpcCall).toRequestBody(APPLICATION_JSON.toMediaType())
        ).build().let {
            client.newCall(it)
        }
    }

    fun <T> decodeResponse(id : Int, rpcCall: RpcCall<T>, response: Response) : T {
        val type = rpcCall.getResultType(rpcCoder.objectMapper.typeFactory)
        return rpcCoder.decode(id, response.body!!.byteStream(), type)
    }

    class Builder private constructor(
         private var target : HttpUrl = "http://127.0.0.1:9933".toHttpUrl(),
         private var basicAuth : String? = null,
         private var client : OkHttpClient = OkHttpClient.Builder().apply {
             callTimeout(Duration.ofMinutes(1))
             followRedirects(false)
         }.build(),
         private var scope : CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
         private var rpcCoder: RpcCoder = RpcCoder(ObjectMapper().registerModule(PolkadotModule())),
         private var onClose : () -> Unit = {
             scope.cancel()
             client.dispatcher.executorService.shutdown()
         } 
     ){
         companion object{
             operator fun invoke(block : Builder.() -> Builder) : OkHttpRpcAdapter {
                 return Builder().apply { block() }.build()
             }

             operator fun invoke() : Builder{
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
        fun target(target: String) = apply { this.target = target.toHttpUrl() }

        /**
         * Setup Basic Auth for RPC calls
         *
         * @param username username
         * @param password password
         * @return builder
         */
         fun basicAuth(username: String, password: String) : Builder{
             return apply {
                 val combine = "$username:$password".toByteArray()
                 basicAuth = "Basic ${Base64.getEncoder().encodeToString(combine)}"
             }
         }

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
         * @return new instance of [OkHttpRpcAdapter]
         */
         fun build() : OkHttpRpcAdapter = OkHttpRpcAdapter(target, basicAuth, client, scope, rpcCoder, onClose)
    }

}

suspend fun <T> OkHttpRpcAdapter.await(rpcCall : RpcCall<T>): T {
    return suspendCancellableCoroutine { continuation ->
        val id = nextId()
        val call = getCall(id, rpcCall)
        continuation.invokeOnCancellation {
            call.cancel()
        }
        call.enqueue(object  : Callback{
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.code != 200){
                    continuation.resumeWithException(RpcException(
                        -32000, "Server returned error status: ${response.code}"
                    ))
                } else if(response.header("content-type", APPLICATION_JSON)?.startsWith(APPLICATION_JSON) == false){
                    continuation.resumeWithException(RpcException(
                        -32000, "Server returned invalid content-type ${response.header("content-type")}"
                    ))
                } else{
                    try{
                        decodeResponse(id, rpcCall, response).let {
                            continuation.resume(it)
                        }
                    }catch (e : Throwable){
                        when(e){
                            is JsonProcessingException -> continuation.resumeWithException(RpcException(-32600, "Unable to encode request as JSON: ${e.message}"))
                            is CompletionException -> continuation.resumeWithException(e.cause ?: e)
                            else -> continuation.resumeWithException(e)
                        }

                    }
                }
            }
        })

    }
}