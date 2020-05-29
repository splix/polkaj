package io.emeraldpay.polkaj.ss58

import io.ipfs.multibase.Base58
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

/**
 * To generate a test vector: subkey --network <substrate/kusama/edgeware/etc> generate
 */
class SS58CodecSpec extends Specification {

    def "Doesn't encode null address type"() {
        when:
        SS58Codec.instance.encode(null, Hex.decodeHex('9053cc32597892cc2cd43ea6e3c0db7a3b4c52e5fe6052762080dbc3e3222c0b'))
        then:
        thrown(IllegalArgumentException)
    }

    def "Doesn't encode null pubkey"() {
        when:
        SS58Codec.instance.encode(SS58Type.Network.SUBSTRATE, null)
        then:
        thrown(IllegalArgumentException)
    }

    def "Doesn't decode null"() {
        when:
        SS58Codec.instance.decode(null)
        then:
        thrown(IllegalArgumentException)
    }

    def "Doesn't decode empty"() {
        when:
        SS58Codec.instance.decode("")
        then:
        thrown(IllegalArgumentException)
    }

    def "Doesn't encode short pubkey"() {
        when:
        SS58Codec.instance.encode(SS58Type.Network.SUBSTRATE, Hex.decodeHex('9053cc32597892cc2cd43ea6e3c0db7a3b4c52e5fe6052762080dbc3e3222c'))
        then:
        thrown(IllegalArgumentException)
    }

    def "Doesn't decode just base struct"() {
        when:
        SS58Codec.instance.decode(Base58.encode([42, 1, 2] as byte[]))
        then:
        def t = thrown(IllegalArgumentException)
        t.message == "Input value is too short"
    }

    def "Doesn't encode long pubkey"() {
        when:
        SS58Codec.instance.encode(SS58Type.Network.SUBSTRATE, Hex.decodeHex('9053cc32597892cc2cd43ea6e3c0db7a3b4c52e5fe6052762080dbc3e3222c0000'))
        then:
        thrown(IllegalArgumentException)
    }

    def "Encode Kusama self-generated"() {
        setup:
        def encoder = SS58Codec.instance
        expect:
        encoder.encode(SS58Type.Network.CANARY, Hex.decodeHex(pubkey)) == addr
        where:
        pubkey                                                             | addr
        '9053cc32597892cc2cd43ea6e3c0db7a3b4c52e5fe6052762080dbc3e3222c0b' | 'FqZJib4Kz759A1VFd2cXX4paQB42w7Uamsyhi4z3kGgCkQy'
        '98c613e5e839fa59e0d27725d315e30526a8509ce512e89d8ba170fc61102a75' | 'G2ddwG6rTNe2X1DhhoW8xiPgqp78MXbZQSfWFnd4FnjwzAy'
        '7e4ae4a99f52dece8416d275b5a0ff98eb04e9eb145bead49039140767d21344' | 'FRuoJj9KBNQDPfAK4expocz6yJa22jVxorfRmzMrx9ki5RD'
        'f6db822fd3cc01dee2baeb1cbd5344c9eb2705dc35334bcc5c10ee56af8fc915' | 'J9zWFLsYZsN2hqze1pMzdyXVGimuDEtsHKfjsj2hdGfA4Lr'
        '8cc1b91adf6d034aaf9fba997dd0d6c919c1eadc6e4772e8f39fc9165d20dd19' | 'FksmaZo839fkhLYkgExc3oLoLT74AgqUByL9HPiVT3h756x'
    }

    def "Encode Edgeware self-generated"() {
        setup:
        def encoder = SS58Codec.instance
        expect:
        encoder.encode(SS58Type.Network.EDGEWARE_BERLIN, Hex.decodeHex(pubkey)) == addr
        where:
        pubkey                                                             | addr
        '80abdc9bc1541a8357bddd866ae7700fa26ecdfcebb02dc858c16350db16fb64' | 'kRKzYtM2KGB9DgonEbuRYdooSDbkyyR8TixhUbvKqsA24mA'
        'aecbef409273b2e0bd8447c8ec005933a3add23990b7a0616103f3db3dea7964' | 'mToiufp2kBkzFSSdtnUzpPU5N8cFU36nnCZ3uYo8RZTS1FR'
        '24774d0a69b55029c4836f800f62cf688f20064715a457e1978b20101b515b73' | 'iLRySFE7iuHYd3mwRCn9UPspdEghLH4FUzd8SDrs1xnLd1S'
    }

    def "Encode Substrate self-generated"() {
        setup:
        def encoder = SS58Codec.instance
        expect:
        encoder.encode(SS58Type.Network.SUBSTRATE, Hex.decodeHex(pubkey)) == addr
        where:
        pubkey                                                             | addr
        'f8c2c616e5d5d805ae14f810da895ed9fe98511c201dc4d4719624a41fb9772c' | '5HgsbKKAqD82bDv25MakEihbS4DXKCdyM76HQFRZYmMdYLcJ'
        'aeac712776ba8e165216861798f8138819dcd4a81388171c81d607c48afb3610' | '5G1jR2ZrhR3zsJF2BwzCwfUHayerHVzXsfr55AH4Eij5wmE2'
        '9266a914bd90b4f6b6ee4b707482ecd69273201c5187d8df2fb3c0bfe5959351' | '5FNfLxMXqFdXZeyxPdMqow1VLfSvgkWegutpjZhS4LT8xFkC'
    }

    def "Decode Substrate self-generated"() {
        setup:
        def encoder = SS58Codec.instance
        expect:
        Hex.encodeHexString(encoder.decode(addr).getValue()) == pubkey
        where:
        pubkey                                                             | addr
        'f8c2c616e5d5d805ae14f810da895ed9fe98511c201dc4d4719624a41fb9772c' | '5HgsbKKAqD82bDv25MakEihbS4DXKCdyM76HQFRZYmMdYLcJ'
        'aeac712776ba8e165216861798f8138819dcd4a81388171c81d607c48afb3610' | '5G1jR2ZrhR3zsJF2BwzCwfUHayerHVzXsfr55AH4Eij5wmE2'
        '9266a914bd90b4f6b6ee4b707482ecd69273201c5187d8df2fb3c0bfe5959351' | '5FNfLxMXqFdXZeyxPdMqow1VLfSvgkWegutpjZhS4LT8xFkC'
    }

    def "Decode Kusama self-generated"() {
        setup:
        def encoder = SS58Codec.instance
        expect:
        Hex.encodeHexString(encoder.decode(addr).getValue()) == pubkey
        where:
        pubkey                                                             | addr
        '9053cc32597892cc2cd43ea6e3c0db7a3b4c52e5fe6052762080dbc3e3222c0b' | 'FqZJib4Kz759A1VFd2cXX4paQB42w7Uamsyhi4z3kGgCkQy'
        '98c613e5e839fa59e0d27725d315e30526a8509ce512e89d8ba170fc61102a75' | 'G2ddwG6rTNe2X1DhhoW8xiPgqp78MXbZQSfWFnd4FnjwzAy'
        '7e4ae4a99f52dece8416d275b5a0ff98eb04e9eb145bead49039140767d21344' | 'FRuoJj9KBNQDPfAK4expocz6yJa22jVxorfRmzMrx9ki5RD'
        'f6db822fd3cc01dee2baeb1cbd5344c9eb2705dc35334bcc5c10ee56af8fc915' | 'J9zWFLsYZsN2hqze1pMzdyXVGimuDEtsHKfjsj2hdGfA4Lr'
        '8cc1b91adf6d034aaf9fba997dd0d6c919c1eadc6e4772e8f39fc9165d20dd19' | 'FksmaZo839fkhLYkgExc3oLoLT74AgqUByL9HPiVT3h756x'
    }

    def "Decode Edgeware self-generated"() {
        setup:
        def encoder = SS58Codec.instance
        expect:
        Hex.encodeHexString(encoder.decode(addr).getValue()) == pubkey
        where:
        pubkey                                                             | addr
        '80abdc9bc1541a8357bddd866ae7700fa26ecdfcebb02dc858c16350db16fb64' | 'kRKzYtM2KGB9DgonEbuRYdooSDbkyyR8TixhUbvKqsA24mA'
        'aecbef409273b2e0bd8447c8ec005933a3add23990b7a0616103f3db3dea7964' | 'mToiufp2kBkzFSSdtnUzpPU5N8cFU36nnCZ3uYo8RZTS1FR'
        '24774d0a69b55029c4836f800f62cf688f20064715a457e1978b20101b515b73' | 'iLRySFE7iuHYd3mwRCn9UPspdEghLH4FUzd8SDrs1xnLd1S'
    }

    def "Throws error for incorrect checksum"() {
        setup:
        def encoder = SS58Codec.instance
        when:
        encoder.decode('kRKzYtM2KGB9DgonEbuRYdooSDbkyyR8TixhUbvKqsA24mB')
        then:
        def t = thrown(IllegalArgumentException)
        t.message == 'Incorrect checksum'
    }
}
