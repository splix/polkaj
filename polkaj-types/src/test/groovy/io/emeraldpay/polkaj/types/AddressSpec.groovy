package io.emeraldpay.polkaj.types


import io.emeraldpay.polkaj.ss58.SS58Type
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class AddressSpec extends Specification {

    def "Cannot create without network"() {
        when:
        new Address(null, Hex.decodeHex('f8c2c616e5d5d805ae14f810da895ed9fe98511c201dc4d4719624a41fb9772c'))
        then:
        def t = thrown(NullPointerException)
        t.message.toLowerCase().contains("network")
    }

    def "Cannot create without pubkey"() {
        when:
        new Address(SS58Type.Network.SUBSTRATE, null)
        then:
        def t = thrown(NullPointerException)
        t.message.toLowerCase().contains("pubkey")
    }

    def "Cannot create with short pubkey"() {
        when:
        new Address(SS58Type.Network.SUBSTRATE, Hex.decodeHex('f8c2c616e5d5d805ae14f810da895ed9fe98511c201dc4d4719624a4'))
        then:
        thrown(IllegalArgumentException)
    }

    def "Parse Address"() {
        expect:
        def act = Address.from(addr)
        Hex.encodeHexString(act.pubkey) == pubkey
        act.network == type
        where:
        pubkey                                                             | addr                                               | type
        'f8c2c616e5d5d805ae14f810da895ed9fe98511c201dc4d4719624a41fb9772c' | '5HgsbKKAqD82bDv25MakEihbS4DXKCdyM76HQFRZYmMdYLcJ' | SS58Type.Network.SUBSTRATE
        'aeac712776ba8e165216861798f8138819dcd4a81388171c81d607c48afb3610' | '5G1jR2ZrhR3zsJF2BwzCwfUHayerHVzXsfr55AH4Eij5wmE2' | SS58Type.Network.SUBSTRATE
        '9266a914bd90b4f6b6ee4b707482ecd69273201c5187d8df2fb3c0bfe5959351' | '5FNfLxMXqFdXZeyxPdMqow1VLfSvgkWegutpjZhS4LT8xFkC' | SS58Type.Network.SUBSTRATE
        '9053cc32597892cc2cd43ea6e3c0db7a3b4c52e5fe6052762080dbc3e3222c0b' | 'FqZJib4Kz759A1VFd2cXX4paQB42w7Uamsyhi4z3kGgCkQy'  | SS58Type.Network.CANARY
        '98c613e5e839fa59e0d27725d315e30526a8509ce512e89d8ba170fc61102a75' | 'G2ddwG6rTNe2X1DhhoW8xiPgqp78MXbZQSfWFnd4FnjwzAy'  | SS58Type.Network.CANARY
        '7e4ae4a99f52dece8416d275b5a0ff98eb04e9eb145bead49039140767d21344' | 'FRuoJj9KBNQDPfAK4expocz6yJa22jVxorfRmzMrx9ki5RD'  | SS58Type.Network.CANARY
    }

    def "Create Address"() {
        expect:
        def act = new Address(type, Hex.decodeHex(pubkey))
        act.toString() == addr
        where:
        pubkey                                                             | addr                                               | type
        'f8c2c616e5d5d805ae14f810da895ed9fe98511c201dc4d4719624a41fb9772c' | '5HgsbKKAqD82bDv25MakEihbS4DXKCdyM76HQFRZYmMdYLcJ' | SS58Type.Network.SUBSTRATE
        'aeac712776ba8e165216861798f8138819dcd4a81388171c81d607c48afb3610' | '5G1jR2ZrhR3zsJF2BwzCwfUHayerHVzXsfr55AH4Eij5wmE2' | SS58Type.Network.SUBSTRATE
        '9266a914bd90b4f6b6ee4b707482ecd69273201c5187d8df2fb3c0bfe5959351' | '5FNfLxMXqFdXZeyxPdMqow1VLfSvgkWegutpjZhS4LT8xFkC' | SS58Type.Network.SUBSTRATE
        '9053cc32597892cc2cd43ea6e3c0db7a3b4c52e5fe6052762080dbc3e3222c0b' | 'FqZJib4Kz759A1VFd2cXX4paQB42w7Uamsyhi4z3kGgCkQy'  | SS58Type.Network.CANARY
        '98c613e5e839fa59e0d27725d315e30526a8509ce512e89d8ba170fc61102a75' | 'G2ddwG6rTNe2X1DhhoW8xiPgqp78MXbZQSfWFnd4FnjwzAy'  | SS58Type.Network.CANARY
        '7e4ae4a99f52dece8416d275b5a0ff98eb04e9eb145bead49039140767d21344' | 'FRuoJj9KBNQDPfAK4expocz6yJa22jVxorfRmzMrx9ki5RD'  | SS58Type.Network.CANARY
    }

    def "Creates empty"() {
        when:
        def act = Address.empty(SS58Type.Network.SUBSTRATE)
        then:
        act != null
        act.network == SS58Type.Network.SUBSTRATE
        Hex.encodeHexString(act.getPubkey()) == '0000000000000000000000000000000000000000000000000000000000000000'
        act.toString() == '5C4hrfjw9DjXZTzV3MwzrrAr9P1MJhSrvWGWqi1eSuyUpnhM'
    }

    def "Cannot update value after creation"() {
        when:
        def pubkey = Hex.decodeHex('f8c2c616e5d5d805ae14f810da895ed9fe98511c201dc4d4719624a41fb9772c')
        def act = new Address(SS58Type.Network.SUBSTRATE, pubkey)

        then:
        act.pubkey == pubkey
        act.pubkey[0] != 0 as byte

        when:
        pubkey[1] = 0 as byte
        then:
        act.pubkey != pubkey
        act.toString() == '5HgsbKKAqD82bDv25MakEihbS4DXKCdyM76HQFRZYmMdYLcJ'
    }

    def "Address equals works"() {
        when:
        def addr1 = new Address(SS58Type.Network.SUBSTRATE, Hex.decodeHex('f8c2c616e5d5d805ae14f810da895ed9fe98511c201dc4d4719624a41fb9772c'))
        def addr2 = Address.from('5HgsbKKAqD82bDv25MakEihbS4DXKCdyM76HQFRZYmMdYLcJ')
        def addr3 = new Address(addr2.getNetwork(), addr2.getPubkey())

        then:
        addr1.equals(addr2)
        addr1.equals(addr3)
        addr2.equals(addr1)
        addr2.equals(addr3)
        addr3.equals(addr1)
        addr3.equals(addr2)

        when:
        def addr4 = new Address(SS58Type.Network.SUBSTRATE_SECONDARY, addr1.getPubkey())

        then:
        !addr1.equals(addr4)
        !addr2.equals(addr4)
        !addr3.equals(addr4)
    }

    def "Has hashCode"() {
        when:
        def addr1 = Address.from('5HgsbKKAqD82bDv25MakEihbS4DXKCdyM76HQFRZYmMdYLcJ')
        def addr2 = Address.from('5G1jR2ZrhR3zsJF2BwzCwfUHayerHVzXsfr55AH4Eij5wmE2')

        then:
        addr1.hashCode() != addr2.hashCode()
    }

    def "Address not equal if different network"() {
        when:
        def addr1 = new Address(SS58Type.Network.SUBSTRATE, Hex.decodeHex('f8c2c616e5d5d805ae14f810da895ed9fe98511c201dc4d4719624a41fb9772c'))
        def addr2 = new Address(SS58Type.Network.SUBSTRATE_SECONDARY, Hex.decodeHex('f8c2c616e5d5d805ae14f810da895ed9fe98511c201dc4d4719624a41fb9772c'))
        def addr3 = new Address(SS58Type.Network.CANARY, Hex.decodeHex('f8c2c616e5d5d805ae14f810da895ed9fe98511c201dc4d4719624a41fb9772c'))

        then:
        addr1 != addr2
        addr1 != addr3
        addr2 != addr3
        addr1 != Address.empty(SS58Type.Network.SUBSTRATE)
        addr3 != Address.empty(SS58Type.Network.CANARY)
    }

    def "Compare as alphanumeric value"() {
        when:
        def act = [
                Address.from('5H1sbKKAqD82bDv25MakEihbS4DXKCdyM76HQFRZYmMdYCBK'),
                Address.from('5H7sbKKAqD82bDv25MakEihbS4DXKCdyM76HQFRZYmMdYHbv'),

                Address.from('5HBsbKKAqD82bDv25MakEihbS4DXKCdyM76HQFRZYmMdYNRW'),
                Address.from('5HPsbKKAqD82bDv25MakEihbS4DXKCdyM76HQFRZYmMdYCuK'),

                Address.from('5HgsbKKAqD82bDv25MakEihbS4DXKCdyM76HQFRZYmMdYLcJ'),
                Address.from('5HksbKKAqD82bDv25MakEihbS4DXKCdyM76HQFRZYmMdYGRK'),
        ]

        then:
        act[0] < act[1]
        act[1] < act[2]
        act[2] < act[3]
        act[3] < act[4]
        act[4] < act[5]
    }

    def "Sorted by encoded value in alphanumeric order (random data)"() {
        setup:
        Random r = new Random()
        byte[] base = new byte[32]
        r.nextBytes(base)
        println("Base: ${Hex.encodeHexString(base)}")
        def addresses = (0..255).collect { s ->
            byte[] c = base.clone()
            c[31] = s.byteValue()
            c
        }.collect {
            new Address(SS58Type.Network.SUBSTRATE, it)
        }
        def strings = addresses.collect { it.toString() }
        Collections.sort(strings)

        when:
        Collections.shuffle(addresses)
        def act = new ArrayList<Address>()
        act.addAll(addresses)
        Collections.sort(act)

        then:
        act.collect {it.toString()}  == strings
    }
}
