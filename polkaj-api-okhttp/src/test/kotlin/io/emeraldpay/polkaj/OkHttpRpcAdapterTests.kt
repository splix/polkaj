package io.emeraldpay.polkaj

import com.fasterxml.jackson.core.JsonGenerationException
import io.emeraldpay.polkaj.api.RpcCall
import io.emeraldpay.polkaj.api.RpcCoder
import io.emeraldpay.polkaj.api.RpcException
import io.emeraldpay.polkaj.apiokhttp.OkHttpRpcAdapter
import io.mockk.*
import kotlinx.coroutines.test.TestCoroutineScope
import okhttp3.Callback
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.InputStream
import java.lang.IllegalStateException
import java.util.concurrent.ExecutionException
import kotlin.test.assertEquals
import kotlin.test.assertIs

class OkHttpRpcAdapterTests {

    private val target = "http://localhost:18080"
    private val client = mockk<OkHttpClient>()
    private val scope = TestCoroutineScope()
    private val rpcCoder = mockk<RpcCoder>(relaxed = true)
    private val onClose = spyk({})
    private val adapter = OkHttpRpcAdapter.Builder{
        target(target)
        client(client)
        scope(scope)
        rpcCoder(rpcCoder)
        onClose(onClose)
        basicAuth("alice", "secret")
    }

    @Test
    fun `the builder uses correct components`(){
        val responseString = "correct"
        val call = RpcCall.create(String::class.java, "chain_getFinalisedHead")
        val listener = CapturingSlot<Callback>()
        every { client.newCall(any()) } returns mockk(relaxed = true){
            every { enqueue(capture(listener)) } answers {
                listener.captured.onResponse(mockk(), mockk(relaxed = true){
                    every { code } returns 200
                    every { header("content-type", any()) } returns "application/json"
                })
            }
        }
        every { rpcCoder.decode<String>(any(), any<InputStream>(), any()) } returns responseString
        val response = adapter.produceRpcFuture(call).get()
        adapter.close()
        verify {
            onClose.invoke()
        }
        assertEquals(responseString, response)
    }

    @Test
    fun `handles JsonProcessingException`(){
        val call = RpcCall.create(String::class.java, "chain_getFinalisedHead")
        val listener = CapturingSlot<Callback>()
        every { client.newCall(any()) } returns mockk(relaxed = true){
            every { enqueue(capture(listener)) } answers {
                listener.captured.onResponse(mockk(), mockk(relaxed = true){
                    every { code } returns 200
                    every { header("content-type", any()) } returns "application/json"
                })
            }
        }
        every { rpcCoder.decode<String>(any(), any<InputStream>(), any()) } throws JsonGenerationException("")
        val throwable = assertThrows<ExecutionException> {
            adapter.produceRpcFuture(call).get()
        }
        val cause = throwable.cause
        assertIs<RpcException>(cause)
        assertEquals(-32600, cause.code)
    }

    @Test
    fun `can not make a call after close`(){
        adapter.close()
        val future = adapter.produceRpcFuture(RpcCall.create(String::class.java, "chain_getFinalisedHead"))
        val throwable = assertThrows<ExecutionException> {
            future.get()
        }
        assertIs<IllegalStateException>(throwable.cause)
    }
}