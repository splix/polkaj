package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.scale.ScaleCodecReader;
import io.emeraldpay.polkaj.scale.ScaleReader;
import io.emeraldpay.polkaj.scale.UnionValue;
import io.emeraldpay.polkaj.scale.reader.UnionReader;
import io.emeraldpay.polkaj.scale.reader.UnsupportedReader;
import io.emeraldpay.polkaj.ss58.SS58Type;
import io.emeraldpay.polkaj.types.Address;

import java.util.Arrays;

public class MultiAddressReader implements ScaleReader<UnionValue<MultiAddress>> {
    private final UnionReader<MultiAddress> reader;

    public MultiAddressReader(SS58Type.Network network) {
        this.reader = new UnionReader<>(
            Arrays.asList(
                new AccountIDReader(network),
                new UnsupportedReader<>("AccountIndex addresses are not supported"),
                new UnsupportedReader<>("Raw addresses are not supported"),
                new UnsupportedReader<>("Address32 addresses are not supported"),
                new UnsupportedReader<>("Address20 addresses are not supported")
            )
        );
    }

    @Override
    public UnionValue<MultiAddress> read(ScaleCodecReader rdr) {
        return rdr.read(reader);
    }

    static class AccountIDReader implements ScaleReader<MultiAddress> {
        private final SS58Type.Network network;

        public AccountIDReader(SS58Type.Network network) {
            this.network = network;
        }

        @Override
        public MultiAddress read(ScaleCodecReader rdr) {
            return new MultiAddress.AccountID(new Address(network, rdr.readUint256()));
        }
    }
}
