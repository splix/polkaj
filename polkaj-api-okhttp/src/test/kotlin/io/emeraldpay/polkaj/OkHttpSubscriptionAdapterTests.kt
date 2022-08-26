package io.emeraldpay.polkaj

import com.fasterxml.jackson.databind.ObjectMapper
import io.emeraldpay.polkaj.api.RpcCall
import io.emeraldpay.polkaj.api.RpcCoder
import io.emeraldpay.polkaj.api.StandardSubscriptions
import io.emeraldpay.polkaj.api.SubscribeCall
import io.emeraldpay.polkaj.apiokhttp.OkHttpSubscriptionAdapter
import io.emeraldpay.polkaj.json.jackson.PolkadotModule
import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.withTimeout
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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
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

    @Test
    fun `handles subscribe id race condition`() : Unit = runBlocking {
        val subResponse = "{\"jsonrpc\":\"2.0\",\"result\":\"EsqruyKPnZvPZ6fr\",\"id\":0}"
        val block = " {\"jsonrpc\":\"2.0\",\"method\":\"chain_newHead\",\"params\":{\"result\":{\"digest\":{\"logs\":[]},\"extrinsicsRoot\":\"0x9869230c3cc05051ce9afef4458d2515fb2141bfd3bdcd88292f41e17ea00ae7\",\"number\":\"0x1d878c\",\"parentHash\":\"0xbe9110f6da6a19ac645a27472e459dcca6eaf4ee4b0b12700ca5d566eea9a638\",\"stateRoot\":\"0x57059722d680b591a469937449df772b95625d4230b39a0a7d855e16d597f168\"},\"subscription\":\"EsqruyKPnZvPZ6fr\"}}\n"
        val sub = StandardSubscriptions.getInstance().newHeads()
        every { webSocket.send(any<String>()) } answers {
            val l = listener.captured
            //simulate subscribe response faster than id for type placed in table
            l.onMessage(webSocket, block) //will not be able to parse block until we give id on next line
            l.onMessage(webSocket, subResponse) // reply with id
            true
        }

        withTimeout(500){
            suspendCoroutine { cont ->
                subscriptionAdapter.subscribe(sub).get().handler {
                    cont.resume(Unit)
                }
            }
        }
    }

    private fun mockResponse(){
        every { webSocket.send(any<String>()) } answers {
            listener.captured.onMessage(webSocket, finalizedHeadResponse)
            true
        }
    }
}