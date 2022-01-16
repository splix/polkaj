package io.emeraldpay.polkaj.scaletypes

import io.emeraldpay.polkaj.scale.ScaleCodecReader
import org.apache.commons.codec.binary.Hex
import spock.lang.Specification

class MetadataV14ReaderSpec extends Specification {

    def "Read Astar"() {
        setup:
        String hex = this.getClass().getClassLoader().getResourceAsStream("metadata-aster.txt").text
        byte[] data = Hex.decodeHex(hex.substring(2))
        when:
        def rdr = new ScaleCodecReader(data)
        def act = rdr.read(new MetadataV14Reader())
        then:
        act.version == 14
        act.magic == 0x6174656d
        act.pallets.size() == 44
        with(act.pallets.find { it.name == "System"}) {
            with(storage.items.find { it.name == "ExtrinsicData"} ) {
                modifier == StorageEntryModifierV12.DEFAULT
                docs == [" Extrinsics data for the current block (maps an extrinsic's index to its data)."]
                type.getId() == MetadataV14.PalletStorageMetadataV14.TypeId.MAP
                with (type.cast(MetadataV14.PalletStorageMetadataV14.MapDefinition).get()) {
                    key.id.intValue() == 4
                    type.id.intValue() == 10
                    hashers == [MetadataV14.PalletStorageMetadataV14.Hasher.TWOX_64_CONCAT]
                }
            }
        }
    }

}
