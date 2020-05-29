package io.emeraldpay.polkaj.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import io.emeraldpay.polkaj.json.MethodsJson
import io.emeraldpay.polkaj.json.RuntimeVersionJson
import io.emeraldpay.polkaj.json.SystemHealthJson
import io.emeraldpay.polkaj.types.Hash256
import io.emeraldpay.polkaj.json.BlockResponseJson
import io.emeraldpay.polkaj.json.jackson.PolkadotModule
import spock.lang.Specification

class StandardCommandsSpec extends Specification {

    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new PolkadotModule())
    TypeFactory typeFactory = objectMapper.typeFactory


    def "Chain get block"() {
        when:
        def act = StandardCommands.getInstance().getBlock(Hash256.from("0x5d83f66b61701da4cbd7a60137db89c69469a4f798b62aba9176ab253b423828"))
        then:
        act.method == "chain_getBlock"
        act.params.toList() == [Hash256.from("0x5d83f66b61701da4cbd7a60137db89c69469a4f798b62aba9176ab253b423828")]
        act.getResultType(typeFactory).getRawClass() == BlockResponseJson.class
    }

    def "Chain get finalized head"() {
        when:
        def act = StandardCommands.getInstance().getFinalizedHead()
        then:
        act.method == "chain_getFinalizedHead"
        act.params.toList() == []
        act.getResultType(typeFactory).getRawClass() == Hash256.class
    }

    def "Chain get runtime version"() {
        when:
        def act = StandardCommands.getInstance().getRuntimeVersion()
        then:
        act.method == "chain_getRuntimeVersion"
        act.params.toList() == []
        act.getResultType(typeFactory).getRawClass() == RuntimeVersionJson.class
    }

    def "Chain get head"() {
        when:
        def act = StandardCommands.getInstance().getHead()
        then:
        act.method == "chain_getHead"
        act.params.toList() == []
        act.getResultType(typeFactory).getRawClass() == Hash256.class
    }

    def "Rpc methods"() {
        when:
        def act = StandardCommands.getInstance().methods()
        then:
        act.method == "rpc_methods"
        act.params.toList() == []
        act.getResultType(typeFactory).getRawClass() == MethodsJson.class
    }

    def "System Chain"() {
        when:
        def act = StandardCommands.getInstance().systemChain()
        then:
        act.method == "system_chain"
        act.params.toList() == []
        act.getResultType(typeFactory).getRawClass() == String.class
    }

    def "System Health"() {
        when:
        def act = StandardCommands.getInstance().systemHealth()
        then:
        act.method == "system_health"
        act.params.toList() == []
        act.getResultType(typeFactory).getRawClass() == SystemHealthJson.class
    }

    def "System Name"() {
        when:
        def act = StandardCommands.getInstance().systemName()
        then:
        act.method == "system_name"
        act.params.toList() == []
        act.getResultType(typeFactory).getRawClass() == String.class
    }

    def "System Node Roles"() {
        when:
        def act = StandardCommands.getInstance().systemNodeRoles()
        then:
        act.method == "system_nodeRoles"
        act.params.toList() == []
        act.getResultType(typeFactory).toCanonical() == "java.util.List<java.lang.String>"
    }

    def "System Peers"() {
        when:
        def act = StandardCommands.getInstance().systemPeers()
        then:
        act.method == "system_peers"
        act.params.toList() == []
        act.getResultType(typeFactory).toCanonical() == "java.util.List<io.emeraldpay.polkaj.json.PeerJson>"
    }

    def "System Version"() {
        when:
        def act = StandardCommands.getInstance().systemVersion()
        then:
        act.method == "system_version"
        act.params.toList() == []
        act.getResultType(typeFactory).getRawClass() == String.class
    }
}
