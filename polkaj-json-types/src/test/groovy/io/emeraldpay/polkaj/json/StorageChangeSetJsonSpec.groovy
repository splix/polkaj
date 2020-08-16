package io.emeraldpay.polkaj.json

import com.fasterxml.jackson.databind.ObjectMapper
import io.emeraldpay.polkaj.types.ByteData
import io.emeraldpay.polkaj.types.Hash256
import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import spock.lang.Specification

class StorageChangeSetJsonSpec extends Specification {

    ObjectMapper objectMapper = JsonSpecCommons.objectMapper

    def "Equals and HashCode work"() {
        when:
        def v = EqualsVerifier.forClass(StorageChangeSetJson)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }

    def "Equals and HashCode work for KeyValueOption"() {
        when:
        def v = EqualsVerifier.forClass(StorageChangeSetJson.KeyValueOption)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
        then:
        v.verify()
    }

    def "Can decode"() {
        setup:
        InputStream json = StorageChangeSetJsonSpec.classLoader.getResourceAsStream("other/storageChange.json")
        when:
        def act = objectMapper.readValue(json, StorageChangeSetJson)
        then:
        act.block == Hash256.from("0xdad79f2e141ea3396c4171a600ed1224871a7383dc874e8aa8c8beddda77babd")
        act.changes.size() == 1
        with(act.changes[0]) {
            key == ByteData.from("0x26aa394eea5630e07c48ae0c9558cef7b99d880ec681799c0cf30e8886371da9de1e86a9a8c739864cf3cc5ec2bea59fd43593c715fdd31c61141abd04a99fd6822c8558854ccde39a5684e7a56da27d")
            data == ByteData.from("0x04000000004b02987fb3b6e00d0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000")
        }

    }
}
