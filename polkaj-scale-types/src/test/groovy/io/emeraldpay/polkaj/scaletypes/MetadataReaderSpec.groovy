package io.emeraldpay.polkaj.scaletypes

import io.emeraldpay.polkaj.scale.ScaleCodecReader
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

import java.nio.ByteBuffer

class MetadataReaderSpec extends Specification {

    def "Read Kusama"() {
        setup:
        String hex = this.getClass().getClassLoader().getResourceAsStream("metadata-kusama.txt").text
        byte[] data = Hex.decodeHex(hex.substring(2))
        when:
        def rdr = new ScaleCodecReader(data)
        def act = rdr.read(new MetadataReader())
        then:
        act.version == 12
        act.magic == 0x6174656d
        act.modules.size() == 30
        with(act.modules.find { it.name == "System"}) {
            with(calls[0]) {
                name == "fill_block"
                arguments.size() == 1
                with(arguments[0]) {
                    name == "_ratio"
                    type == "Perbill"
                }
                documentation == [" A dispatch that will fill the block weight up to the given ratio."]
            }
            with(calls[1]) {
                name == "remark"
                arguments.size() == 1
                with(arguments[0]) {
                    name == "_remark"
                    type == "Vec<u8>"
                }
                documentation == [" Make some on-chain remark.",
                                  "",
                                  " # <weight>",
                                  " - `O(1)`",
                                  " - Base Weight: 0.665 µs, independent of remark length.",
                                  " - No DB operations.",
                                  " # </weight>"]
            }
        }
        with(act.modules.find { it.name == "Treasury"}) {
            with(events[0]) {
                name == "Proposed"
            }
            with(constants.find {it.name == "ModuleId"}) {
                type == "ModuleId"
                new String(value) == "py/trsry"
            }
            with(constants.find {it.name == "ProposalBondMinimum"}) {
                type == "BalanceOf<T, I>"
                value.with {
                    def copy = it.clone().toList()
                    Collections.reverse(copy)
                    ByteBuffer.wrap(copy.toArray() as byte[], 8, 8).long
                } == 3333333333320L
            }
        }
        with(act.modules.find { it.name == "Grandpa"}) {
            calls.size() == 3
            events.size() == 3
            storage.entries.size() == 6
            errors.size() == 7
            with(errors[0]) {
                name == "PauseFailed"
            }
        }
        with(act.modules.find { it.name == "Democracy"}) {
            with(storage.entries.find { it.name == "DepositOf"} ) {
                modifier == Metadata.Storage.Modifier.OPTIONAL
                documentation == [" Those who have locked a deposit.",
                                  "",
                                  " TWOX-NOTE: Safe, as increasing integer keys are safe."]
                type.getId() == Metadata.Storage.TypeId.MAP
                with (type.cast(Metadata.Storage.MapDefinition).get()) {
                    key == "PropIndex"
                    type == "(Vec<T::AccountId>, BalanceOf<T>)"
                    hasher == Metadata.Storage.Hasher.TWOX_64_CONCAT
                    !iterable
                }
            }
        }
    }

    def "Correct module index"() {
        setup:
        String hex = this.getClass().getClassLoader().getResourceAsStream("metadata-kusama.txt").text
        byte[] data = Hex.decodeHex(hex.substring(2))
        when:
        def rdr = new ScaleCodecReader(data)
        def act = rdr.read(new MetadataReader())
        then:
        act.findCall("System", "fill_block").get().index == 0x0000
        act.findCall("System", "set_storage").get().index == 0x0006
        act.findCall("Balances", "transfer").get().index == 0x0400
        act.findCall("Balances", "force_transfer").get().index == 0x0402
        act.findCall("Democracy", "propose").get().index == 0x0d00
        act.findCall("Democracy", "fast_track").get().index == 0x0d07
        act.findCall("Democracy", "enact_proposal").get().index == 0x0d16
        act.findCall("Vesting", "vest").get().index == 0x1c00
        act.findCall("Vesting", "vested_transfer").get().index == 0x1c02
    }

}
