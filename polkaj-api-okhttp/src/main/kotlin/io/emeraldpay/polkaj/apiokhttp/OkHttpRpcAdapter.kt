package io.emeraldpay.polkaj.apiokhttp

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.emeraldpay.polkaj.api.RpcCall
import io.emeraldpay.polkaj.api.RpcCallAdapter
import io.emeraldpay.polkaj.api.RpcCoder
import io.emeraldpay.polkaj.api.RpcException
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
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OkHttpRpcAdapter(
    private val target : HttpUrl,
    private val basicAuth : String?,
    private val client : OkHttpClient,
    private val scope : CoroutineScope,
    private val rpcCoder: RpcCoder,
    private val onClose : () -> Unit
) : RpcCallAdapter {

    companion object{
        private const val APPLICATION_JSON = "application/json"
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

    suspend fun <T> await(rpcCall : RpcCall<T>): T {
        return suspendCancellableCoroutine { continuation ->
            val id = rpcCoder.nextId()
            val type = rpcCall.getResultType(rpcCoder.objectMapper.typeFactory)
            val call = baseRequest.newBuilder().post(
                rpcCoder.encode(id, rpcCall).toRequestBody(APPLICATION_JSON.toMediaType())
            ).build().let {
                client.newCall(it)
            }
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
                            rpcCoder.decode<T>(id, response.body!!.byteStream(), type).let {
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

     data class Builder(
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
             inline operator fun invoke(block : Builder.() -> Builder) : OkHttpRpcAdapter {
                 return Builder().apply { block() }.build()
             }

         }

        fun target(target: String) = apply { this.target = target.toHttpUrl() }

         fun basicAuth(username: String, password: String) : Builder{
             return apply {
                 val combine = "$username:$password".toByteArray()
                 basicAuth = "Basic ${Base64.getEncoder().encodeToString(combine)}"
             }
         }

         fun client(client : OkHttpClient) = apply { this.client = client }
         fun scope(scope : CoroutineScope) = apply { this.scope = scope }
         fun rpcCoder(rpcCoder : RpcCoder) = apply { this.rpcCoder = rpcCoder }
         fun onClose(block : () -> Unit ) = apply { onClose = block }
         fun timeout(timeout : Duration) = apply { client = client.newBuilder().callTimeout(timeout).build() }
         fun build() : OkHttpRpcAdapter = OkHttpRpcAdapter(target, basicAuth, client, scope, rpcCoder, onClose)
    }

}