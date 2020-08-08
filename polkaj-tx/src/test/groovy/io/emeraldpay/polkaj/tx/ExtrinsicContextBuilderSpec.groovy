package io.emeraldpay.polkaj.tx

import io.emeraldpay.polkaj.types.DotAmount
import io.emeraldpay.polkaj.types.Hash256
import spock.lang.Specification

class ExtrinsicContextBuilderSpec extends Specification {

    def "Default build"() {
        when:
        def act = ExtrinsicContext.newBuilder()
                .build()
        then:
        act.nonce == 0
        act.era.immortal
        act.eraBlockHash == Hash256.empty()
        act.eraHeight == 0
        act.genesis == Hash256.empty()
        act.tip == DotAmount.ZERO
        act.runtimeVersion == 254
        act.txVersion == 1
    }

    def "Set genesis"() {
        when:
        def act = ExtrinsicContext.newBuilder()
                .genesis(Hash256.from("0x35170a58d341fd81c07ee349438da400ecfb625782cd25e29774203080a54f45"))
                .build()
        then:
        act.genesis == Hash256.from("0x35170a58d341fd81c07ee349438da400ecfb625782cd25e29774203080a54f45")
    }

    def "Set genesis for immortal sets era hash"() {
        when:
        def act = ExtrinsicContext.newBuilder()
                .genesis(Hash256.from("0x35170a58d341fd81c07ee349438da400ecfb625782cd25e29774203080a54f45"))
                .build()
        then:
        act.era.immortal
        act.eraBlockHash == Hash256.from("0x35170a58d341fd81c07ee349438da400ecfb625782cd25e29774203080a54f45")
    }

    def "Set runtime"() {
        when:
        def act = ExtrinsicContext.newBuilder()
                .runtime(10, 34)
                .build()
        then:
        act.txVersion == 10
        act.runtimeVersion == 34
    }

    def "Set nonce"() {
        when:
        def act = ExtrinsicContext.newBuilder()
                .nonce(50161)
                .build()
        then:
        act.nonce == 50161
    }

    def "Set tip"() {
        when:
        def act = ExtrinsicContext.newBuilder()
                .tip(DotAmount.fromDots(1.2))
                .build()
        then:
        act.tip == DotAmount.fromDots(1.2)
    }

}
