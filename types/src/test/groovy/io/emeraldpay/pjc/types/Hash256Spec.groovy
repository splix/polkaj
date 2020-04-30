package io.emeraldpay.pjc.types

import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class Hash256Spec extends Specification {

    def "Create"() {
        when:
        def act = new Hash256(Hex.decodeHex("63c2499de640b43c924bc2bfc9ea89730e7c4790e24d126906e7af6c99cb506b"))
        then:
        Hex.encodeHexString(act.bytes) == "63c2499de640b43c924bc2bfc9ea89730e7c4790e24d126906e7af6c99cb506b"
    }

    def "Create empty"() {
        when:
        def act = Hash256.empty()
        then:
        Hex.encodeHexString(act.bytes) == "0000000000000000000000000000000000000000000000000000000000000000"
    }

    def "Cannot update after creation"() {
        setup:
        byte[] bytes = Hex.decodeHex("63c2499de640b43c924bc2bfc9ea89730e7c4790e24d126906e7af6c99cb506b")
        when:
        def act = new Hash256(bytes)
        bytes[0] = 0
        then:
        Hex.encodeHexString(act.bytes) == "63c2499de640b43c924bc2bfc9ea89730e7c4790e24d126906e7af6c99cb506b"
    }

    def "Cannot update by getting value"() {
        when:
        def act = new Hash256(Hex.decodeHex("63c2499de640b43c924bc2bfc9ea89730e7c4790e24d126906e7af6c99cb506b"))
        def bytes = act.getBytes()
        bytes[0] = 0
        then:
        Hex.encodeHexString(act.bytes) == "63c2499de640b43c924bc2bfc9ea89730e7c4790e24d126906e7af6c99cb506b"
    }

    def "Cannot create with null value"() {
        when:
        new Hash256(null)
        then:
        thrown(NullPointerException)
    }

    def "Cannot create with short value"() {
        when:
        new Hash256(Hex.decodeHex("934e28fe5329e45c65fd363204c14147e44bace6d090b9e894e476ac"))
        then:
        thrown(IllegalArgumentException)
    }

    def "Cannot create with long value"() {
        when:
        new Hash256(Hex.decodeHex("f27adeca2eb5997bb2e4b9a966f8fa3dddd5e119a169db626a7b37223f2961f91baabf9dd33d0e34dfc023c730417afb"))
        then:
        thrown(IllegalArgumentException)
    }

    def "Creates from hex"() {
        expect:
        def act = Hash256.from(hex)
        Hex.encodeHexString(act.bytes) == "4179076e56ae774db30082f3edfdfefd2267b21232915a2d33780a57fe4e0b24"
        where:
        hex << [
                "4179076e56ae774db30082f3edfdfefd2267b21232915a2d33780a57fe4e0b24",
                "4179076E56AE774DB30082F3EDFDFEFD2267B21232915A2D33780A57FE4E0B24",
                "0x4179076e56ae774db30082f3edfdfefd2267b21232915a2d33780a57fe4e0b24",
                "0x4179076E56AE774DB30082F3EDFDFEFD2267B21232915A2D33780A57FE4E0B24",
        ]
    }

    def "Creates from hex with zero prefix"() {
        when:
        def act = Hash256.from("000003339ffc68ceadfaa7331cca3328e834102cdf9bb1dd64f684660cba1ace")
        then:
        act.toString() == "000003339ffc68ceadfaa7331cca3328e834102cdf9bb1dd64f684660cba1ace"
    }

    def "Creates from hex with zero suffix"() {
        when:
        def act = Hash256.from("ad883a34521b676f0e66d32cf6391fbbdaa5c67f60b43de89f3fc669f6d4e000")
        then:
        act.toString() == "ad883a34521b676f0e66d32cf6391fbbdaa5c67f60b43de89f3fc669f6d4e000"
    }

    def "Creates max value"() {
        when:
        def act = Hash256.from("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff")
        then:
        act.toString() == "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
    }

    def "Creates min value"() {
        when:
        def act = Hash256.from("0000000000000000000000000000000000000000000000000000000000000000")
        then:
        act.toString() == "0000000000000000000000000000000000000000000000000000000000000000"
    }

    def "Cannot creates from invalid hex"() {
        when:
        Hash256.from("K179076e56ae774db30082f3edfdfefd2267b21232915a2d33780a57fe4e0b24")
        then:
        thrown(NumberFormatException)
    }

    def "Cannot creates from invalid hex prefix"() {
        when:
        Hash256.from("0b4179076e56ae774db30082f3edfdfefd2267b21232915a2d33780a57fe4e0b24")
        then:
        thrown(IllegalArgumentException)
    }

    def "Cannot creates from long hex"() {
        when:
        Hash256.from("f27adeca2eb5997bb2e4b9a966f8fa3dddd5e119a169db626a7b37223f2961f91baabf9dd33d0e34dfc023c730417afb")
        then:
        thrown(IllegalArgumentException)
    }

    def "Cannot creates from short hex"() {
        when:
        Hash256.from("934e28fe5329e45c65fd363204c14147e44bace6d090b9e894e476ac")
        then:
        thrown(IllegalArgumentException)
    }

    def "Cannot creates from null"() {
        when:
        Hash256.from(null)
        then:
        thrown(NullPointerException)
    }

    def "toString produces hex"() {
        when:
        def act = Hash256.from("63c2499de640b43c924bc2bfc9ea89730e7c4790e24d126906e7af6c99cb506b")
        then:
        act.toString() == "63c2499de640b43c924bc2bfc9ea89730e7c4790e24d126906e7af6c99cb506b"
    }

    def "Same hashes are equal"() {
        when:
        def hash1 = Hash256.from("63c2499de640b43c924bc2bfc9ea89730e7c4790e24d126906e7af6c99cb506b")
        def hash2 = Hash256.from("63c2499de640b43c924bc2bfc9ea89730e7c4790e24d126906e7af6c99cb506b")
        then:
        hash1.equals(hash2)
    }

    def "Same hashes are same compared"() {
        when:
        def hash1 = Hash256.from("63c2499de640b43c924bc2bfc9ea89730e7c4790e24d126906e7af6c99cb506b")
        def hash2 = Hash256.from("63c2499de640b43c924bc2bfc9ea89730e7c4790e24d126906e7af6c99cb506b")
        then:
        hash1.compareTo(hash2) == 0
    }

    def "Same hashes have same hashCode"() {
        when:
        def hash1 = Hash256.from("63c2499de640b43c924bc2bfc9ea89730e7c4790e24d126906e7af6c99cb506b")
        def hash2 = Hash256.from("63c2499de640b43c924bc2bfc9ea89730e7c4790e24d126906e7af6c99cb506b")
        then:
        hash1.hashCode() == hash2.hashCode()
    }

    def "Diff hashes are not equal"() {
        when:
        def hash1 = Hash256.from("63c2499de640b43c924bc2bfc9ea89730e7c4790e24d126906e7af6c99cb506b")
        def hash2 = Hash256.from("73c2499de640b43c924bc2bfc9ea89730e7c4790e24d126906e7af6c99cb506b")
        then:
        !hash1.equals(hash2)
    }

    def "Diff hashes are ordered"() {
        when:
        def hash1 = Hash256.from("63c2499de640b43c924bc2bfc9ea89730e7c4790e24d126906e7af6c99cb506b")
        def hash2 = Hash256.from("73c2499de640b43c924bc2bfc9ea89730e7c4790e24d126906e7af6c99cb506b")
        then:
        hash1 < hash2
    }

    def "Diff hashes have diff hashCode"() {
        when:
        def hash1 = Hash256.from("63c2499de640b43c924bc2bfc9ea89730e7c4790e24d126906e7af6c99cb506b")
        def hash2 = Hash256.from("73c2499de640b43c924bc2bfc9ea89730e7c4790e24d126906e7af6c99cb506b")
        then:
        hash1.hashCode() != hash2.hashCode()
    }
}
