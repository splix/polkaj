package io.emeraldpay.polkaj.json

import com.fasterxml.jackson.databind.ObjectMapper
import io.emeraldpay.polkaj.types.Address
import io.emeraldpay.polkaj.types.ByteData
import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification

class ContractCallRequestJsonSpec extends Specification {

    ObjectMapper objectMapper = JsonSpecCommons.objectMapper

    def "Serialize basic"() {
        setup:
        def obj = new ContractCallRequestJson().tap {
            origin = Address.from("5G1jR2ZrhR3zsJF2BwzCwfUHayerHVzXsfr55AH4Eij5wmE2")
            dest = Address.from("5FNfLxMXqFdXZeyxPdMqow1VLfSvgkWegutpjZhS4LT8xFkC")
            value = 101
            gasLimit = 250000
        }
        when:
        def act = objectMapper.writeValueAsString(obj)

        then:
        act == '{"origin":"5G1jR2ZrhR3zsJF2BwzCwfUHayerHVzXsfr55AH4Eij5wmE2","dest":"5FNfLxMXqFdXZeyxPdMqow1VLfSvgkWegutpjZhS4LT8xFkC","value":101,"gasLimit":250000,"inputData":"0x"}'
    }

    def "Serialize with data"() {
        setup:
        def obj = new ContractCallRequestJson().tap {
            origin = Address.from("5G1jR2ZrhR3zsJF2BwzCwfUHayerHVzXsfr55AH4Eij5wmE2")
            dest = Address.from("5FNfLxMXqFdXZeyxPdMqow1VLfSvgkWegutpjZhS4LT8xFkC")
            value = 101
            gasLimit = 250000
            inputData = ByteData.from("0x1020")
        }
        when:
        def act = objectMapper.writeValueAsString(obj)

        then:
        act == '{"origin":"5G1jR2ZrhR3zsJF2BwzCwfUHayerHVzXsfr55AH4Eij5wmE2","dest":"5FNfLxMXqFdXZeyxPdMqow1VLfSvgkWegutpjZhS4LT8xFkC","value":101,"gasLimit":250000,"inputData":"0x1020"}'
    }

    def "Deserialize basic"() {
        setup:
        def json = '{"origin":"5G1jR2ZrhR3zsJF2BwzCwfUHayerHVzXsfr55AH4Eij5wmE2","dest":"5FNfLxMXqFdXZeyxPdMqow1VLfSvgkWegutpjZhS4LT8xFkC","value":101,"gasLimit":250000,"inputData":"0x"}'
        def exp = new ContractCallRequestJson().tap {
            origin = Address.from("5G1jR2ZrhR3zsJF2BwzCwfUHayerHVzXsfr55AH4Eij5wmE2")
            dest = Address.from("5FNfLxMXqFdXZeyxPdMqow1VLfSvgkWegutpjZhS4LT8xFkC")
            value = 101
            gasLimit = 250000
        }

        when:
        def act = objectMapper.readValue(json, ContractCallRequestJson)
        then:
        act == exp
    }

    def "Cannot set null data"() {
        setup:
        def obj = new ContractCallRequestJson().tap {
            origin = Address.from("5G1jR2ZrhR3zsJF2BwzCwfUHayerHVzXsfr55AH4Eij5wmE2")
            dest = Address.from("5FNfLxMXqFdXZeyxPdMqow1VLfSvgkWegutpjZhS4LT8xFkC")
            value = 101
            gasLimit = 250000
        }
        when:
        obj.setInputData(null)
        then:
        thrown(NullPointerException)
    }

    def "Equals and HashCode works"() {
        when:
        def v = EqualsVerifier.forClass(ContractCallRequestJson)
                .withPrefabValues(
                        Address,
                        Address.from("5G1jR2ZrhR3zsJF2BwzCwfUHayerHVzXsfr55AH4Eij5wmE2"),
                        Address.from("5FNfLxMXqFdXZeyxPdMqow1VLfSvgkWegutpjZhS4LT8xFkC")
                )
                .withNonnullFields("inputData")
                // for a library the class should not be final
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }
}
