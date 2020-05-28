package io.emeraldpay.pjc.json

import com.fasterxml.jackson.databind.ObjectMapper
import io.emeraldpay.pjc.types.Hash256
import spock.lang.Specification

class PeerJsonSpec extends Specification {

    ObjectMapper objectMapper = JsonSpecCommons.objectMapper
    def type = objectMapper.typeFactory.constructCollectionLikeType(List.class, PeerJson.class)

    def "Deserialize"() {
        setup:
        InputStream json = BlockJsonSpec.classLoader.getResourceAsStream("system/peers.json")
        when:
        def act = objectMapper.readValue(json, type) as List<PeerJson>
        then:
        act != null
        act.size() == 4
        with(act[0]) {
            bestHash == Hash256.from("0xf8aa3c720af4ee1d5d6399c5ec519c93ea3fd6accdd89b5cad9a17aaa2386bf5")
            bestNumber == 2498544
            peerId == "12D3KooWEGHw84b4hfvXEfyq4XWEmWCbRGuHMHQMpby4BAtZ4xJf"
            protocolVersion == 6
            roles == "FULL"
        }
        with(act[1]) {
            bestHash == Hash256.from("0xf8aa3c720af4ee1d5d6399c5ec519c93ea3fd6accdd89b5cad9a17aaa2386bf5")
            bestNumber == 2498544
            peerId == "12D3KooWCzvSFZp2tD6CAkwm3zXLiDDCnG8M7DuNagXM3UQvqM55"
            protocolVersion == 6
            roles == "AUTHORITY"
        }
        with(act[2]) {
            bestHash == Hash256.from("0xf8aa3c720af4ee1d5d6399c5ec519c93ea3fd6accdd89b5cad9a17aaa2386bf5")
            bestNumber == 2498544
            peerId == "12D3KooWF9KDPRMN8WpeyXhEeURZGP8Dmo7go1tDqi7hTYpxV9uW"
            protocolVersion == 6
            roles == "FULL"
        }
        with(act[3]) {
            bestHash == Hash256.from("0x93a44c200f8a8ed7026f5f46c831e56440a46fdb3bfaca6dee085db8926f8538")
            bestNumber == 2498542
            peerId == "12D3KooWDgtynm4S9M3m6ZZhXYu2RrWKdvkCSScc25xKDVSg1Sjd"
            protocolVersion == 6
            roles == "FULL"
        }
    }

    def "Same data are equal"() {
        setup:
        String json = BlockJsonSpec.classLoader.getResourceAsStream("system/peers.json").text
        when:
        def x = objectMapper.readValue(json, type)
        def y = objectMapper.readValue(json, type)
        then:
        x == y
        x.hashCode() == y.hashCode()
    }

    def "Diff are not equal"() {
        setup:
        String json = BlockJsonSpec.classLoader.getResourceAsStream("system/peers.json").text
        when:
        def x = objectMapper.readValue(json, type) as List<PeerJson>
        def y = (objectMapper.readValue(json, type) as List<PeerJson>).tap {
            it[0].protocolVersion--
        }
        then:
        x != y
    }

}
