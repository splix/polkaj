package io.emeraldpay.polkaj.json

import com.fasterxml.jackson.databind.ObjectMapper
import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification

class RuntimeDispatchInfoJsonSpec extends Specification {

    ObjectMapper objectMapper = JsonSpecCommons.objectMapper

    def "Deserialize normal"() {
        setup:
        InputStream json = RuntimeDispatchInfoJsonSpec.classLoader.getResourceAsStream("other/paymentInfo.json")
        when:
        def act = objectMapper.readValue(json, RuntimeDispatchInfoJson)
        then:
        act != null
        act.weight == 195000000
        act.partialFee.value == BigInteger.valueOf(166532583)
        act.getDispatchClass() == RuntimeDispatchInfoJson.DispatchClass.NORMAL
    }

    def "Equals"() {
        when:
        def v = EqualsVerifier.forClass(RuntimeDispatchInfoJson)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }
}
