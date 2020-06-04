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
        act.version == 11
        act.magic == 0x6174656d
        act.modules.size() == 33
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
                documentation == [" Make some on-chain remark.", "", " # <weight>", " - `O(1)`",  " # </weight>"]
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
                type == "BalanceOf<T>"
                value.with {
                    def copy = it.clone().toList()
                    Collections.reverse(copy)
                    ByteBuffer.wrap(copy.toArray() as byte[], 8, 8).long
                } == 20000000000000L
            }
        }
        with(act.modules.find { it.name == "Grandpa"}) {
            calls.size() == 1
            events.size() == 3
            storage.entries.size() == 6
            errors.size() == 4
            with(errors[0]) {
                name == "PauseFailed"
            }
        }
        with(act.modules.find { it.name == "Democracy"}) {
            with(storage.entries.find { it.name == "DepositOf"} ) {
                modifier == Metadata.Storage.Modifier.OPTIONAL
                documentation == [" Those who have locked a deposit."]
                type.getId() == Metadata.Storage.TypeId.MAP
                with (type.cast(Metadata.Storage.MapDefinition).get()) {
                    key == "PropIndex"
                    type == "(BalanceOf<T>, Vec<T::AccountId>)"
                    hasher == Metadata.Storage.Hasher.TWOX_64_CONCAT
                    !iterable
                }
            }

        }
    }

}
