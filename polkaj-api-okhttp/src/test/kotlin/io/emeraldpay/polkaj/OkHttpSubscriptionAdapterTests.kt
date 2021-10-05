package io.emeraldpay.polkaj

import com.fasterxml.jackson.databind.ObjectMapper
import io.emeraldpay.polkaj.api.RpcCall
import io.emeraldpay.polkaj.api.RpcCoder
import io.emeraldpay.polkaj.apiokhttp.OkHttpSubscriptionAdapter
import io.emeraldpay.polkaj.json.jackson.PolkadotModule
import io.mockk.*
import kotlinx.coroutines.test.TestCoroutineScope
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Duration
import java.util.concurrent.ExecutionException
import kotlin.test.assertEquals

class OkHttpSubscriptionAdapterTests {

    private val target = "ws://localhost:9999"
    private val scope = TestCoroutineScope()
    private val rpcCoder = mockk<RpcCoder>(relaxed = true){
        every { objectMapper } returns ObjectMapper().apply { registerModule(PolkadotModule()) }
    }
    private val client = mockk<OkHttpClient>(relaxed = true)
    private val onClose = spyk({})
    private val webSocket  = mockk<WebSocket>()

    private val listener = CapturingSlot<WebSocketListener>()
    private val request = CapturingSlot<Request>()

    private val finalizedHeadResponse = """{ 
            "jsonrpc" : "2.0",
             "result" : "0x5d83f66b61701da4cbd7a60137db89c69469a4f798b62aba9176ab253b423828",
             "id" : 0
        }
    """.trimIndent()

    private val subscriptionAdapter = OkHttpSubscriptionAdapter.Builder {
        timeout(Duration.ofSeconds(1))
        connectTo(target)
        scope(scope)
        rpcCoder(rpcCoder)
        client(client)
        onClose(onClose)
    }

    @BeforeEach
    fun setup(){
        every {
            client.newWebSocket(capture(request), capture(listener))
        } answers {
            listener.captured.onOpen(webSocket, mockk())
            webSocket
        }

        every { rpcCoder.nextId() } returns 0
    }

    @AfterEach
    fun cleanup(){
        scope.cleanupTestCoroutines()
    }

    @Test
    fun `client connects automatically`(){
        mockResponse()
        val call = RpcCall.create(String::class.java, "chain_getFinalisedHead")
        subscriptionAdapter.produceRpcFuture(call).get()
        verify(exactly = 1) {
            client.newWebSocket(any(), any())
        }
    }

    @Test
    fun `client doesn't make a second websocket when already connected`(){
        mockResponse()
        val call = RpcCall.create(String::class.java, "chain_getFinalisedHead")
        subscriptionAdapter.produceRpcFuture(call).get()
        subscriptionAdapter.produceRpcFuture(call).get()
        verify(exactly = 1) {
            client.newWebSocket(any(), any())
        }
    }

    @Test
    fun `adapter closes given onClose method`(){
        mockResponse()
        every { webSocket.close(any(), any()) } returns true
        val call = RpcCall.create(String::class.java, "chain_getFinalisedHead")
        subscriptionAdapter.produceRpcFuture(call).get()
        subscriptionAdapter.close()
        verify {
            onClose.invoke()
            webSocket.close(eq(1000), eq("close"))
        }
    }

    @Test
    fun `socket failure given to pending request`(){
        val exception = Exception()
        val call = RpcCall.create(String::class.java, "chain_getFinalisedHead")
        every { webSocket.send(any<String>()) } answers {
            listener.captured.onMessage(webSocket, finalizedHeadResponse)
            true
        } andThenAnswer {
            listener.captured.onFailure(webSocket, exception, null)
            true
        }
        subscriptionAdapter.produceRpcFuture(call).get()
        val future = subscriptionAdapter.produceRpcFuture(call)

        val caught = assertThrows<ExecutionException> {
            future.get()
        }
        assertEquals(exception, caught.cause?.cause)
    }

    private fun mockResponse(){
        every { webSocket.send(any<String>()) } answers {
            listener.captured.onMessage(webSocket, finalizedHeadResponse)
            true
        }
    }
}