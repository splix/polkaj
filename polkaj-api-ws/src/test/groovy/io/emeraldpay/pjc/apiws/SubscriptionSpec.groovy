package io.emeraldpay.pjc.apiws


import spock.lang.Specification

import java.util.concurrent.CompletableFuture

class SubscriptionSpec extends Specification {

    def "Id is immutable"() {
        when:
        def s = new Subscription(null, "test", null)
        s.setId(101)
        then:
        s.getId() == 101

        when:
        s.setId(102)
        then:
        thrown(IllegalStateException)
    }

    def "Accept is silent when no handler"() {
        when:
        def s = new Subscription(null, "test", null)
        s.accept(new Subscription.Event("test", "test"))
        then:
        notThrown(NullPointerException)
    }

    def "Accept calls handler handler"() {
        when:
        def s = new Subscription(null, "test", null)
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
        def s = new Subscription(null, "untest", client)
        s.setId(10)
        s.close()
        then:
        1 * client.execute(Boolean.class, "untest", [10]) >> CompletableFuture.completedFuture(true)
        1 * client.removeSubscription(10)
    }

    def "Close does nothing if not initialized"() {
        setup:
        def client = Mock(PolkadotWsApi)
        when:
        def s = new Subscription(null, "untest", client)
        s.close()
        then:
        0 * client.execute(_, _, _) >> CompletableFuture.completedFuture(false)
        0 * client.removeSubscription(_)
    }
}
