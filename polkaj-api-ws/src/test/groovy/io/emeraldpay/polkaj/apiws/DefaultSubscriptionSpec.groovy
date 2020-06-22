package io.emeraldpay.polkaj.apiws

import io.emeraldpay.polkaj.api.RpcCall
import io.emeraldpay.polkaj.api.Subscription
import spock.lang.Specification

import java.util.concurrent.CompletableFuture

class DefaultSubscriptionSpec extends Specification {

    def "Id is immutable"() {
        when:
        def s = new DefaultSubscription(null, "test", null)
        s.setId("EsqruyKPnZvPZ6fr")
        then:
        s.getId() == "EsqruyKPnZvPZ6fr"

        when:
        s.setId("EsqruyKPnZvPZ6fr")
        then:
        thrown(IllegalStateException)
    }

    def "Accept is silent when no handler"() {
        when:
        def s = new DefaultSubscription(null, "test", null)
        s.accept(new Subscription.Event("test", "test"))
        then:
        notThrown(NullPointerException)
    }

    def "Accept calls handler handler"() {
        when:
        def s = new DefaultSubscription(null, "test", null)
        Subscription.Event handled = null
        s.handler({
            handled = it
        })
        s.accept(new Subscription.Event("test", "test"))
        then:
        handled == new Subscription.Event("test", "test")
    }

    def "Close unsubscribes and self-removes"() {
        setup:
        def client = Mock(PolkadotWsApi)
        when:
        def s = new DefaultSubscription(null, "untest", client)
        s.setId("EsqruyKPnZvPZ6fr")
        s.close()
        then:
        1 * client.execute(RpcCall.create(Boolean.class, "untest", ["EsqruyKPnZvPZ6fr"])) >> CompletableFuture.completedFuture(true)
        1 * client.removeSubscription("EsqruyKPnZvPZ6fr")
    }

    def "Close does nothing if not initialized"() {
        setup:
        def client = Mock(PolkadotWsApi)
        when:
        def s = new DefaultSubscription(null, "untest", client)
        s.close()
        then:
        0 * client.execute(_) >> CompletableFuture.completedFuture(false)
        0 * client.removeSubscription(_)
    }
}
