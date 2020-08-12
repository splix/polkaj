package io.emeraldpay.polkaj.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import io.emeraldpay.polkaj.json.ContractCallRequestJson
import io.emeraldpay.polkaj.json.ContractExecResultJson
import io.emeraldpay.polkaj.json.MethodsJson
import io.emeraldpay.polkaj.json.RuntimeDispatchInfoJson
import io.emeraldpay.polkaj.json.RuntimeVersionJson
import io.emeraldpay.polkaj.json.SystemHealthJson
import io.emeraldpay.polkaj.types.Address
import io.emeraldpay.polkaj.types.ByteData
import io.emeraldpay.polkaj.types.Hash256
import io.emeraldpay.polkaj.json.BlockResponseJson
import io.emeraldpay.polkaj.json.jackson.PolkadotModule
import org.apache.commons.codec.binary.Hex
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

    def "Chain get block hash"() {
        when:
        def act = StandardCommands.getInstance().getBlockHash()
        then:
        act.method == "chain_getBlockHash"
        act.params.toList() == []
        act.getResultType(typeFactory).getRawClass() == Hash256.class

        when:
        act = StandardCommands.getInstance().getBlockHash(101)
        then:
        act.method == "chain_getBlockHash"
        act.params.toList() == [101]
        act.getResultType(typeFactory).getRawClass() == Hash256.class
    }

    def "Chain get finalized head"() {
        when:
        def act = StandardCommands.getInstance().getFinalizedHead()
        then:
        act.method == "chain_getFinalizedHead"
        act.params.toList() == []
        act.getResultType(typeFactory).getRawClass() == Hash256.class
    }

    def "State get runtime version"() {
        when:
        def act = StandardCommands.getInstance().getRuntimeVersion()
        then:
        act.method == "state_getRuntimeVersion"
        act.params.toList() == []
        act.getResultType(typeFactory).getRawClass() == RuntimeVersionJson.class
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

    def "State Metadata"() {
        when:
        def act = StandardCommands.getInstance().stateMetadata()
        then:
        act.method == "state_getMetadata"
        act.params.toList() == []
        act.getResultType(typeFactory).getRawClass() == ByteData.class
    }

    def "State Get Storage"() {
        when:
        def act = StandardCommands.getInstance().stateGetStorage(Hex.decodeHex("0102"))
        then:
        act.method == "state_getStorage"
        act.params.toList() == ["0x0102"]
        act.getResultType(typeFactory).getRawClass() == ByteData.class

        when:
        act = StandardCommands.getInstance().stateGetStorage( ByteData.from("0x0102"))
        then:
        act.method == "state_getStorage"
        act.params.toList() == ["0x0102"]
        act.getResultType(typeFactory).getRawClass() == ByteData.class
    }

    def "Contracts Call"() {
        setup:
        def call = new ContractCallRequestJson()
        when:
        def act = StandardCommands.getInstance().contractsCall(call)
        then:
        act.method == "contracts_call"
        act.params.toList() == [call]
        act.getResultType(typeFactory).getRawClass() == ContractExecResultJson

        when:
        act = StandardCommands.getInstance().contractsCall(call, null)
        then:
        act.method == "contracts_call"
        act.params.toList() == [call]
        act.getResultType(typeFactory).getRawClass() == ContractExecResultJson

        when:
        act = StandardCommands.getInstance().contractsCall(call, Hash256.from("0x814d23726176d151037f13c6371967795cadcb56400b4fb443248278180c10f0"))
        then:
        act.method == "contracts_call"
        act.params.toList() == [call, Hash256.from("0x814d23726176d151037f13c6371967795cadcb56400b4fb443248278180c10f0")]
        act.getResultType(typeFactory).getRawClass() == ContractExecResultJson
    }

    def "Contracts Get Storage"() {
        when:
        def act = StandardCommands.getInstance().contractsGetStorage(
                Address.from("FqZJib4Kz759A1VFd2cXX4paQB42w7Uamsyhi4z3kGgCkQy"),
                Hash256.from("0x814d23726176d151037f13c6371967795cadcb56400b4fb443248278180c10f0")
        )
        then:
        act.method == "contracts_getStorage"
        act.params.toList() == [
                Address.from("FqZJib4Kz759A1VFd2cXX4paQB42w7Uamsyhi4z3kGgCkQy"),
                Hash256.from("0x814d23726176d151037f13c6371967795cadcb56400b4fb443248278180c10f0")
        ]
        act.getResultType(typeFactory).getRawClass() == ByteData.class

        when:
        act = StandardCommands.getInstance().contractsGetStorage(
                Address.from("FqZJib4Kz759A1VFd2cXX4paQB42w7Uamsyhi4z3kGgCkQy"),
                Hash256.from("0x814d23726176d151037f13c6371967795cadcb56400b4fb443248278180c10f0"),
                null
        )
        then:
        act.method == "contracts_getStorage"
        act.params.toList() == [
                Address.from("FqZJib4Kz759A1VFd2cXX4paQB42w7Uamsyhi4z3kGgCkQy"),
                Hash256.from("0x814d23726176d151037f13c6371967795cadcb56400b4fb443248278180c10f0")
        ]
        act.getResultType(typeFactory).getRawClass() == ByteData.class

        when:
        act = StandardCommands.getInstance().contractsGetStorage(
                Address.from("FqZJib4Kz759A1VFd2cXX4paQB42w7Uamsyhi4z3kGgCkQy"),
                Hash256.from("0x814d23726176d151037f13c6371967795cadcb56400b4fb443248278180c10f0"),
                Hash256.from("0x5c51037f13c637196779564726176d10f000b4fb443248278180c1adcb814d23")
        )
        then:
        act.method == "contracts_getStorage"
        act.params.toList() == [
                Address.from("FqZJib4Kz759A1VFd2cXX4paQB42w7Uamsyhi4z3kGgCkQy"),
                Hash256.from("0x814d23726176d151037f13c6371967795cadcb56400b4fb443248278180c10f0"),
                Hash256.from("0x5c51037f13c637196779564726176d10f000b4fb443248278180c1adcb814d23")
        ]
        act.getResultType(typeFactory).getRawClass() == ByteData.class
    }

    def "Contracts Rent Projection"() {
        when:
        def act = StandardCommands.getInstance().contractsRentProjection(Address.from("FqZJib4Kz759A1VFd2cXX4paQB42w7Uamsyhi4z3kGgCkQy"))
        then:
        act.method == "contracts_rentProjection"
        act.params.toList() == [Address.from("FqZJib4Kz759A1VFd2cXX4paQB42w7Uamsyhi4z3kGgCkQy")]
        act.getResultType(typeFactory).getRawClass() == Long.class

        when:
        act = StandardCommands.getInstance().contractsRentProjection(Address.from("FqZJib4Kz759A1VFd2cXX4paQB42w7Uamsyhi4z3kGgCkQy"), null)
        then:
        act.method == "contracts_rentProjection"
        act.params.toList() == [Address.from("FqZJib4Kz759A1VFd2cXX4paQB42w7Uamsyhi4z3kGgCkQy")]
        act.getResultType(typeFactory).getRawClass() == Long.class

        when:
        act = StandardCommands.getInstance().contractsRentProjection(
                Address.from("FqZJib4Kz759A1VFd2cXX4paQB42w7Uamsyhi4z3kGgCkQy"),
                Hash256.from("0x5c51037f13c637196779564726176d10f000b4fb443248278180c1adcb814d23"))
        then:
        act.method == "contracts_rentProjection"
        act.params.toList() == [
                Address.from("FqZJib4Kz759A1VFd2cXX4paQB42w7Uamsyhi4z3kGgCkQy"),
                Hash256.from("0x5c51037f13c637196779564726176d10f000b4fb443248278180c1adcb814d23")
        ]
        act.getResultType(typeFactory).getRawClass() == Long.class
    }

    def "Author Submit Extrinsic"() {
        when:
        def act = StandardCommands.getInstance().authorSubmitExtrinsic(ByteData.from("0x0011223344"))
        then:
        act.method == "author_submitExtrinsic"
        act.params.toList() == [
                ByteData.from("0x0011223344")
        ]
        act.getResultType(typeFactory).getRawClass() == Hash256.class

        when:
        StandardCommands.getInstance().authorSubmitExtrinsic(null)
        then:
        thrown(NullPointerException)

        when:
        StandardCommands.getInstance().authorSubmitExtrinsic(ByteData.empty())
        then:
        thrown(IllegalArgumentException)
    }

    def "Author Pending Extrinsics"() {
        when:
        def act = StandardCommands.getInstance().authorPendingExtrinsics()
        then:
        act.method == "author_pendingExtrinsics"
        act.params.toList() == []
        act.getResultType(typeFactory).toCanonical() == "java.util.List<io.emeraldpay.polkaj.types.ByteData>"
    }

    def "Author Remove Extrinsic"() {
        when:
        def act = StandardCommands.getInstance().authorRemoveExtrinsic(
                Hash256.from("0xa65ccbdb5ee9b6ff39f9e1869e891430f1040db9a8130aa3b7f412e50f80b60a")
        )
        then:
        act.method == "author_removeExtrinsic"
        act.params.toList() == [
                Hash256.from("0xa65ccbdb5ee9b6ff39f9e1869e891430f1040db9a8130aa3b7f412e50f80b60a")
        ]
        act.getResultType(typeFactory).toCanonical() == "java.util.List<io.emeraldpay.polkaj.types.Hash256>"

        when:
        act = StandardCommands.getInstance().authorRemoveExtrinsic(
                Hash256.from("0xa65ccbdb5ee9b6ff39f9e1869e891430f1040db9a8130aa3b7f412e50f80b60a"),
                Hash256.from("0x430f1040db9a8130aa3b7f412e50f80b60aa65ccbdb5ee9b6ff39f9e1869e891")
        )
        then:
        act.method == "author_removeExtrinsic"
        act.params.toList() == [
                Hash256.from("0xa65ccbdb5ee9b6ff39f9e1869e891430f1040db9a8130aa3b7f412e50f80b60a"),
                Hash256.from("0x430f1040db9a8130aa3b7f412e50f80b60aa65ccbdb5ee9b6ff39f9e1869e891")
        ]
        act.getResultType(typeFactory).toCanonical() == "java.util.List<io.emeraldpay.polkaj.types.Hash256>"

        when:
        act = StandardCommands.getInstance().authorRemoveExtrinsic(
                ByteData.from("0xa65ccbdb5ee9b6ff39f9e1869e8914"),
                ByteData.from("0x430f1040db9a8130aa3b7f412e50f80b60")
        )
        then:
        act.method == "author_removeExtrinsic"
        act.params.toList() == [
                ByteData.from("0xa65ccbdb5ee9b6ff39f9e1869e8914"),
                ByteData.from("0x430f1040db9a8130aa3b7f412e50f80b60")
        ]
        act.getResultType(typeFactory).toCanonical() == "java.util.List<io.emeraldpay.polkaj.types.Hash256>"
    }

    def "Author Has Key"() {
        when:
        def act = StandardCommands.getInstance().authorHasKey(
                Address.from("5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY"),
                "test"
        )
        then:
        act.method == "author_hasKey"
        act.params.toList() == [
                ByteData.from("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d"),
                "test"
        ]
        act.getResultType(typeFactory).getRawClass() == Boolean
    }

    def "Author Has Session Keys"() {
        when:
        def act = StandardCommands.getInstance().authorHasSessionKeys(
                ByteData.from("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d")
        )
        then:
        act.method == "author_hasSessionKeys"
        act.params.toList() == [
                ByteData.from("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d"),
        ]
        act.getResultType(typeFactory).getRawClass() == Boolean
    }

    def "Author Insert Key"() {
        when:
        def act = StandardCommands.getInstance().authorInsertKey(
                "test",
                "foobar",
                Address.from("5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY"),
        )
        then:
        act.method == "author_insertKey"
        act.params.toList() == [
                "test",
                "foobar",
                ByteData.from("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d"),
        ]
        act.getResultType(typeFactory).getRawClass() == ByteData
    }

    def "Author Rotate Keys"() {
        when:
        def act = StandardCommands.getInstance().authorRotateKeys()
        then:
        act.method == "author_rotateKeys"
        act.params.toList() == []
        act.getResultType(typeFactory).getRawClass() == ByteData
    }

    def "Account Next Index"() {
        when:
        def act = StandardCommands.getInstance().accountNextIndex(Address.from("5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY"))
        then:
        act.method == "account_nextIndex"
        act.params.toList() == [
                Address.from("5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY")
        ]
        act.getResultType(typeFactory).getRawClass() == Integer
    }

    def "Payment Query Info"() {
        when:
        def act = StandardCommands.getInstance().paymentQueryInfo(
                ByteData.from("0x12345678")
        )
        then:
        act.method == "payment_queryInfo"
        act.params.toList() == [
                ByteData.from("0x12345678")
        ]
        act.getResultType(typeFactory).getRawClass() == RuntimeDispatchInfoJson

        when:
        act = StandardCommands.getInstance().paymentQueryInfo(
                ByteData.from("0x12345678"),
                Hash256.from("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d")
        )
        then:
        act.method == "payment_queryInfo"
        act.params.toList() == [
                ByteData.from("0x12345678"),
                Hash256.from("0xd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d")
        ]
        act.getResultType(typeFactory).getRawClass() == RuntimeDispatchInfoJson
    }
}
