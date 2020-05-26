package io.emeraldpay.pjc.apiws


import io.emeraldpay.pjc.api.RpcResponse
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
        def act = WsResponse.subscription(new PolkadotWsApi.SubscriptionResponse<Object>(1, "test", "test"))
        then:
        act.getType() == WsResponse.Type.SUBSCRIPTION
        act.getValue() == new PolkadotWsApi.SubscriptionResponse<Object>(1, "test", "test")
        act.asEvent() == new PolkadotWsApi.SubscriptionResponse<Object>(1, "test", "test")
    }

    def "Cannot cast rcp to event"() {
        when:
        WsResponse.rpc(new RpcResponse<Object>(1, "test")).asEvent()
        then:
        thrown(ClassCastException)
    }

    def "Cannot cast event to rpc"() {
        when:
        WsResponse.subscription(new PolkadotWsApi.SubscriptionResponse<Object>(1, "test", "test")).asRpc()
        then:
        thrown(ClassCastException)
    }

}
