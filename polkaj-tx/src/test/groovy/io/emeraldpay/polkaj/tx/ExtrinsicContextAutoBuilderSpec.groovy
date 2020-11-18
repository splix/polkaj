package io.emeraldpay.polkaj.tx

import io.emeraldpay.polkaj.api.PolkadotApi
import io.emeraldpay.polkaj.api.StandardCommands
import io.emeraldpay.polkaj.json.RuntimeVersionJson
import io.emeraldpay.polkaj.types.ByteData
import io.emeraldpay.polkaj.types.Hash256
import spock.lang.Specification

import java.util.concurrent.CompletableFuture

class ExtrinsicContextAutoBuilderSpec extends Specification {

    def "Build all from api"() {
        setup:
        AccountRequests.AddressBalance requestAccount = AccountRequests.balanceOf(TestKeys.alice);
        def api = Mock(PolkadotApi) {
            1 * execute(
                    StandardCommands.getInstance().stateGetStorage(requestAccount.encodeRequest())
            ) >> CompletableFuture.completedFuture(
                    // 1,000,000.00 Dot, nonce = 1
                    ByteData.from("0x010000000000000019e4759db3b6e00d0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000")
            )
            1 * execute(
                    StandardCommands.getInstance().getBlockHash(0)
            ) >> CompletableFuture.completedFuture(
                    Hash256.from("0x4c0bdd177c17ca145ad9a3e76d092d4d4baa8add4fa8c78cc2fbbf8e3cbd5122")
            )
            1 * execute(
                    StandardCommands.getInstance().getRuntimeVersion()
            ) >> CompletableFuture.completedFuture(
                    new RuntimeVersionJson().tap {
                        setTransactionVersion(101)
                        setSpecVersion(202)
                    }
            )
        }
        def builder = new ExtrinsicContext.AutoBuilder(TestKeys.alice)
        when:
        def act = builder.fetch(api).get().build()
        then:
        act.genesis == Hash256.from("0x4c0bdd177c17ca145ad9a3e76d092d4d4baa8add4fa8c78cc2fbbf8e3cbd5122")
        act.nonce == 1
        act.runtimeVersion == 202
        act.txVersion == 101
        act.era == Era.IMMORTAL
    }
}
