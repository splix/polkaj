package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.scale.ScaleCodecReader;
import io.emeraldpay.polkaj.scale.ScaleReader;

public class AccountDataReader implements ScaleReader<AccountData> {

    @Override
    public AccountData read(ScaleCodecReader rdr) {
        AccountData result = new AccountData();
        result.setFree(rdr.read(BalanceReader.INSTANCE));
        result.setReserved(rdr.read(BalanceReader.INSTANCE));
        result.setMiscFrozen(rdr.read(BalanceReader.INSTANCE));
        result.setFeeFrozen(rdr.read(BalanceReader.INSTANCE));
        return result;
    }
}
