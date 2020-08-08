package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.scale.ScaleCodecReader;
import io.emeraldpay.polkaj.scale.ScaleReader;
import io.emeraldpay.polkaj.ss58.SS58Type;
import io.emeraldpay.polkaj.types.Address;
import io.emeraldpay.polkaj.types.DotAmount;

public class BalanceTransferReader implements ScaleReader<BalanceTransfer> {

    private final SS58Type.Network network;

    public BalanceTransferReader(SS58Type.Network network) {
        this.network = network;
    }

    @Override
    public BalanceTransfer read(ScaleCodecReader rdr) {
        BalanceTransfer result = new BalanceTransfer();
        result.setModuleIndex(rdr.readUByte());
        result.setCallIndex(rdr.readUByte());
        result.setDestination(new Address(network, rdr.readUint256()));
        result.setBalance(new DotAmount(rdr.read(ScaleCodecReader.COMPACT_BIGINT)));
        return result;
    }
}
