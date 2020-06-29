package io.emeraldpay.polkaj.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import io.emeraldpay.polkaj.json.RuntimeVersionJson
import io.emeraldpay.polkaj.json.BlockJson
import io.emeraldpay.polkaj.json.StorageChangeSetJson
import io.emeraldpay.polkaj.json.jackson.PolkadotModule
import io.emeraldpay.polkaj.types.ByteData
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

    def "state subscribe runtime version"() {
        when:
        def act = StandardSubscriptions.getInstance().runtimeVersion()
        then:
        act.method == "state_subscribeRuntimeVersion"
        act.params.size() == 0
        act.unsubscribe == "state_unsubscribeRuntimeVersion"
        act.getResultType(typeFactory).getRawClass() == RuntimeVersionJson.class
    }

    def "state subscribe storage"() {
        when:
        def act = StandardSubscriptions.getInstance().storage()
        then:
        act.method == "state_subscribeStorage"
        act.params.size() == 0
        act.unsubscribe == "state_unsubscribeStorage"
        act.getResultType(typeFactory).getRawClass() == StorageChangeSetJson.class

        when:
        act = StandardSubscriptions.getInstance().storage(null)
        then:
        act.method == "state_subscribeStorage"
        act.params.size() == 0
        act.unsubscribe == "state_unsubscribeStorage"
        act.getResultType(typeFactory).getRawClass() == StorageChangeSetJson.class

        when:
        act = StandardSubscriptions.getInstance().storage([])
        then:
        act.method == "state_subscribeStorage"
        act.params.size() == 0
        act.unsubscribe == "state_unsubscribeStorage"
        act.getResultType(typeFactory).getRawClass() == StorageChangeSetJson.class

        when:
        act = StandardSubscriptions.getInstance().storage([ByteData.from("0x00")])
        then:
        act.method == "state_subscribeStorage"
        act.params.toList() == [[ByteData.from("0x00")]]
        act.unsubscribe == "state_unsubscribeStorage"
        act.getResultType(typeFactory).getRawClass() == StorageChangeSetJson.class
    }
}
