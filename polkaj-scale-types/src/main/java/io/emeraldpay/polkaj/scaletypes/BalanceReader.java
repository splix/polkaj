package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.scale.ScaleCodecReader;
import io.emeraldpay.polkaj.scale.ScaleReader;
import io.emeraldpay.polkaj.types.DotAmount;

/**
 * Decode balance encoded as uint128
 */
public class BalanceReader implements ScaleReader<DotAmount> {

    public static final BalanceReader INSTANCE = new BalanceReader();

    @Override
    public DotAmount read(ScaleCodecReader rdr) {
        return new DotAmount(rdr.readUint128());
    }
}
