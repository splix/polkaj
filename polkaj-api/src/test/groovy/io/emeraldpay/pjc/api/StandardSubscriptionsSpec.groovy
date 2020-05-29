package io.emeraldpay.pjc.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import io.emeraldpay.pjc.json.BlockJson
import io.emeraldpay.pjc.json.jackson.PolkadotModule
import spock.lang.Specification

class StandardSubscriptionsSpec extends Specification {

    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new PolkadotModule())
    TypeFactory typeFactory = objectMapper.typeFactory


    def "chain subscribe new heads"() {
        when:
        def act = StandardSubscriptions.getInstance().newHeads()
        then:
        act.method == "chain_subscribeNewHead"
        act.params.size() == 0
        act.unsubscribe == "chain_unsubscribeNewHead"
        act.getResultType(typeFactory).getRawClass() == BlockJson.Header.class
    }

    def "chain subscribe finalized heads"() {
        when:
        def act = StandardSubscriptions.getInstance().finalizedHeads()
        then:
        act.method == "chain_subscribeFinalizedHeads"
        act.params.size() == 0
        act.unsubscribe == "chain_unsubscribeFinalizedHeads"
        act.getResultType(typeFactory).getRawClass() == BlockJson.Header.class
    }
}
