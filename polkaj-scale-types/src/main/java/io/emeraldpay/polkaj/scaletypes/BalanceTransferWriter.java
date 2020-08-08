package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.scale.ScaleCodecWriter;
import io.emeraldpay.polkaj.scale.ScaleWriter;

import java.io.IOException;

public class BalanceTransferWriter implements ScaleWriter<BalanceTransfer> {

    @Override
    public void write(ScaleCodecWriter wrt, BalanceTransfer value) throws IOException {
        wrt.writeByte(value.getModuleIndex());
        wrt.writeByte(value.getCallIndex());
        wrt.writeUint256(value.getDestination().getPubkey());
        wrt.write(ScaleCodecWriter.COMPACT_BIGINT, value.getBalance().getValue());
    }
}
