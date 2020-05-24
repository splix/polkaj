package io.emeraldpay.pjc.clientws

import io.emeraldpay.pjc.client.RpcResponse
import spock.lang.Specification

class WsResponseSpec extends Specification {

    def "Creates rpc response"() {
        when:
        def act = WsResponse.rpc(new RpcResponse<Object>(1, "test"))
        then:
        act.getType() == WsResponse.Type.RPC
        act.getValue() == new RpcResponse<Object>(1, "test")
        act.asRpc() == new RpcResponse<Object>(1, "test")
    }

    def "Creates subscription response"() {
        when:
        def act = WsResponse.subscription(new PolkadotWsClient.SubscriptionResponse<Object>(1, "test", "test"))
        then:
        act.getType() == WsResponse.Type.SUBSCRIPTION
        act.getValue() == new PolkadotWsClient.SubscriptionResponse<Object>(1, "test", "test")
        act.asEvent() == new PolkadotWsClient.SubscriptionResponse<Object>(1, "test", "test")
    }

    def "Cannot cast rcp to event"() {
        when:
        WsResponse.rpc(new RpcResponse<Object>(1, "test")).asEvent()
        then:
        thrown(ClassCastException)
    }

    def "Cannot cast event to rpc"() {
        when:
        WsResponse.subscription(new PolkadotWsClient.SubscriptionResponse<Object>(1, "test", "test")).asRpc()
        then:
        thrown(ClassCastException)
    }

}
