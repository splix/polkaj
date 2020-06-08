package io.emeraldpay.polkaj.json

import com.fasterxml.jackson.databind.ObjectMapper
import io.emeraldpay.polkaj.types.Hash256
import io.emeraldpay.polkaj.types.ByteData
import spock.lang.Specification

class BlockJsonSpec extends Specification {

    ObjectMapper objectMapper = JsonSpecCommons.objectMapper

    def "Deserialize empty"() {
        setup:
        InputStream json = BlockJsonSpec.classLoader.getResourceAsStream("blocks/0x0.json")
        when:
        def act = objectMapper.readValue(json, BlockJson)
        then:
        act != null
        act.extrinsics == []
        act.header != null
        with(act.header) {
            number == 0
            extrinsicsRoot.toString() == "0x03170a2e7597b7b7e3d84c05391d139a62b157e78786d8c082f29dcf4c111314"
            parentHash.toString() == "0x0000000000000000000000000000000000000000000000000000000000000000"
            stateRoot.toString() == "0xb0006203c3a6e6bd2c6a17b1d4ae8ca49a31da0f4579da950b127774b44aef6b"
        }
    }

    def "Deserialize basic"() {
        setup:
        InputStream json = BlockJsonSpec.classLoader.getResourceAsStream("blocks/0x59d78.json")
        when:
        def act = objectMapper.readValue(json, BlockJson)
        then:
        act != null
        act.extrinsics.size() == 4
        act.extrinsics[0].toString() == "0x280402000b6083ea396f01"
        act.extrinsics[1].toString() == "0x1c040900da751600"
        act.extrinsics[2].toString() == "0x1004140000"
        act.extrinsics[3].toString() == "0x4d0284ff495e1e506f266418af07fa0c5c108dd436f2faa59fe7d9e54403779f5bbd771800a77072f82ecd1a4db50d63013cce0a54b0b9760c38a1104f26ee08db59c9f04e9d2304a21903dfcf40de55c304ef4f6496f24cbc76aa8827b23757f02571b6084917bd03000400ffac96718c7347d2383954e1b252e91d9b549cee2c3c92b0a6aa3660aa7cc951380f00103849d05b06"
        act.header != null
        with(act.header) {
            with(digest) {
                logs.size() == 2
                logs[0].toString() == "0x0642414245b5010123000000fa16ab0f000000008cfe4c54209c84416293523e20650a01b36eaa040c89c9a1749e5b57eeb4dc36749df99ea5468a64b00cd7338022c2172105aafe8655471dcb98ae55612a6605bfe4e28c10800d3ecbf1c51741507b7b0a97fd645324d331cbc987212279bc09"
                logs[1].toString() == "0x05424142450101dc055b9657de69cae005abb8b19b3d56717a946cf58504e808dcdb8a0439790091afa83253f4f4588a5c4f3084856400a6a84fe2ffb3831e0e1bfbf12a7ed78d"
            }
            number == 0x59d78
            extrinsicsRoot.toString() == "0xf2783b2fcf79a633890cef76a1edcad79bcec8a0e2744b87a3047d08812b68d2"
            parentHash.toString() == "0xe5d10dec5eb8156cccad3c8cc9493c73f93b663ae00ec5242021b9a456b84198"
            stateRoot.toString() == "0x9623f79d8bd2248c2777f88e2e5ee9063b1a2991cfab15a97c11f7f89d6e97e9"
        }
    }

    def "Serialize empty"() {
        setup:
        String exp = BlockJsonSpec.classLoader.getResourceAsStream("blocks/0x0.json").text
                .with {
                    objectMapper.writeValueAsString(objectMapper.readValue(it, Map))
                }
        BlockJson block = new BlockJson().tap {
            extrinsics = []
            header = new BlockJson.Header().tap {
                digest = new BlockJson.Header.Digest()
                extrinsicsRoot = Hash256.from("0x03170a2e7597b7b7e3d84c05391d139a62b157e78786d8c082f29dcf4c111314")
                number = 0
                parentHash = Hash256.from("0x0000000000000000000000000000000000000000000000000000000000000000")
                stateRoot = Hash256.from("0xb0006203c3a6e6bd2c6a17b1d4ae8ca49a31da0f4579da950b127774b44aef6b")
            }
        }
        when:
        def act = objectMapper.writeValueAsString(block)
        then:
        act == exp
    }

    def "Serialize basic"() {
        setup:
        String exp = BlockJsonSpec.classLoader.getResourceAsStream("blocks/0x59d78.json").text
                .with {
                    objectMapper.writeValueAsString(objectMapper.readValue(it, Map))
                }
        BlockJson block = new BlockJson().tap {
            extrinsics = [
                    ByteData.from("0x280402000b6083ea396f01"),
                    ByteData.from("0x1c040900da751600"),
                    ByteData.from("0x1004140000"),
                    ByteData.from("0x4d0284ff495e1e506f266418af07fa0c5c108dd436f2faa59fe7d9e54403779f5bbd771800a77072f82ecd1a4db50d63013cce0a54b0b9760c38a1104f26ee08db59c9f04e9d2304a21903dfcf40de55c304ef4f6496f24cbc76aa8827b23757f02571b6084917bd03000400ffac96718c7347d2383954e1b252e91d9b549cee2c3c92b0a6aa3660aa7cc951380f00103849d05b06")
            ]
            header = new BlockJson.Header().tap {
                digest = new BlockJson.Header.Digest().tap {
                    logs = [
                            ByteData.from("0x0642414245b5010123000000fa16ab0f000000008cfe4c54209c84416293523e20650a01b36eaa040c89c9a1749e5b57eeb4dc36749df99ea5468a64b00cd7338022c2172105aafe8655471dcb98ae55612a6605bfe4e28c10800d3ecbf1c51741507b7b0a97fd645324d331cbc987212279bc09"),
                            ByteData.from("0x05424142450101dc055b9657de69cae005abb8b19b3d56717a946cf58504e808dcdb8a0439790091afa83253f4f4588a5c4f3084856400a6a84fe2ffb3831e0e1bfbf12a7ed78d")
                    ]
                }
                extrinsicsRoot = Hash256.from("0xf2783b2fcf79a633890cef76a1edcad79bcec8a0e2744b87a3047d08812b68d2")
                number = 0x59d78
                parentHash = Hash256.from("0xe5d10dec5eb8156cccad3c8cc9493c73f93b663ae00ec5242021b9a456b84198")
                stateRoot = Hash256.from("0x9623f79d8bd2248c2777f88e2e5ee9063b1a2991cfab15a97c11f7f89d6e97e9")
            }
        }
        when:
        def act = objectMapper.writeValueAsString(block)
        then:
        act == exp
    }

    def "Same are equal"() {
        setup:
        String json = BlockJsonSpec.classLoader.getResourceAsStream("blocks/0x59d78.json").text
        when:
        def act1 = objectMapper.readValue(json, BlockJson)
        def act2 = objectMapper.readValue(json, BlockJson)
        then:
        act1 == act2
        act1 == act1
        act1.hashCode() == act2.hashCode()
    }

    def "Diff are not equal"() {
        setup:
        String json = BlockJsonSpec.classLoader.getResourceAsStream("blocks/0x59d78.json").text
        when:
        def act1 = objectMapper.readValue(json, BlockJson)
        def act2 = objectMapper.readValue(json, BlockJson).tap {
            it.extrinsics.add(ByteData.from("0x00"))
        }
        def act3 = objectMapper.readValue(json, BlockJson).tap {
            it.header.number++
        }
        def act4 = objectMapper.readValue(json, BlockJson).tap {
            it.header.digest.logs.remove(0)
        }
        def act5 = objectMapper.readValue(json, BlockJson).tap {
            it.header.digest = null
        }
        def act6 = objectMapper.readValue(json, BlockJson).tap {
            it.header.extrinsicsRoot = null
        }
        def act7 = objectMapper.readValue(json, BlockJson).tap {
            it.header.parentHash = null
        }
        def act8 = objectMapper.readValue(json, BlockJson).tap {
            it.header.stateRoot = null
        }
        then:
        act1 != act2
        act1 != act3
        act1 != act4
        act2 != act3
        act2 != act4
        act3 != act4
        act1 != act5
        act4 != act5
        act1 != act6
        act3 != act6
        act1 != act7
        act1 != act8
    }

}
