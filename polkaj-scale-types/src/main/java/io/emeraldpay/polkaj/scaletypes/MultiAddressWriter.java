package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.scale.ScaleCodecWriter;
import io.emeraldpay.polkaj.scale.ScaleWriter;
import io.emeraldpay.polkaj.scale.UnionValue;
import io.emeraldpay.polkaj.scale.writer.UnionWriter;

import java.io.IOException;
import java.util.Arrays;

public class MultiAddressWriter implements ScaleWriter<UnionValue<MultiAddress>> {
    private static final UnionWriter<MultiAddress> WRITER = new UnionWriter<>(
        Arrays.asList(
            new AccountIDWriter()
        )
    );

    @Override
    public void write(ScaleCodecWriter wrt, UnionValue<MultiAddress> value) throws IOException {
        wrt.write(WRITER, value);
    }

    static class AccountIDWriter implements ScaleWriter<MultiAddress> {
        @Override
        public void write(ScaleCodecWriter wrt, MultiAddress value) throws IOException {
            MultiAddress.AccountID accountID = (MultiAddress.AccountID) value;
            wrt.writeUint256(accountID.getAddress().getPubkey());
        }
    }
}
