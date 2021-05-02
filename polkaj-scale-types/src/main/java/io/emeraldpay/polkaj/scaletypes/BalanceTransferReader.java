package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.scale.ScaleCodecReader;
import io.emeraldpay.polkaj.scale.ScaleReader;
import io.emeraldpay.polkaj.ss58.SS58Type;
import io.emeraldpay.polkaj.types.Address;
import io.emeraldpay.polkaj.types.DotAmount;

public class BalanceTransferReader implements ScaleReader<BalanceTransfer> {

    private final MultiAddressReader destinationReader;

    public BalanceTransferReader(SS58Type.Network network) {
        this.destinationReader = new MultiAddressReader(network);
    }

    @Override
    public BalanceTransfer read(ScaleCodecReader rdr) {
        BalanceTransfer result = new BalanceTransfer();
        result.setModuleIndex(rdr.readUByte());
        result.setCallIndex(rdr.readUByte());
        result.setDestination(rdr.read(destinationReader));
        result.setBalance(new DotAmount(rdr.read(ScaleCodecReader.COMPACT_BIGINT)));
        return result;
    }
}
