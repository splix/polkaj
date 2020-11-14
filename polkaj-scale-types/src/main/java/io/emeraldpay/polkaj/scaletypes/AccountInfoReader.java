package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.scale.ScaleCodecReader;
import io.emeraldpay.polkaj.scale.ScaleReader;

public class AccountInfoReader implements ScaleReader<AccountInfo> {
    @Override
    public AccountInfo read(ScaleCodecReader rdr) {
        AccountInfo result = new AccountInfo();
        result.setNonce(rdr.readUint32());
        result.setRefcount(rdr.readUint32());
        result.setData(rdr.read(new AccountDataReader()));
        return result;
    }
}
