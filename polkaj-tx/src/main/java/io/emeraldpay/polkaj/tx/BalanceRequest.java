package io.emeraldpay.polkaj.tx;

import io.emeraldpay.polkaj.scale.ScaleCodecReader;
import io.emeraldpay.polkaj.scaletypes.AccountInfo;
import io.emeraldpay.polkaj.scaletypes.AccountInfoReader;
import io.emeraldpay.polkaj.scaletypes.BalanceReader;
import io.emeraldpay.polkaj.types.Address;
import io.emeraldpay.polkaj.types.ByteData;
import io.emeraldpay.polkaj.types.DotAmount;

import java.nio.ByteBuffer;

public class BalanceRequest {

    public static TotalIssuance totalIssuance() {
        return new TotalIssuance();
    }

    public static AddressBalance balanceOf(Address address) {
        return new AddressBalance(address);
    }

    public static class TotalIssuance implements StorageRequest<DotAmount> {

        @Override
        public ByteData requestData() {
            String key1 = "Balances";
            String key2 = "TotalIssuance";
            int len = 16 + 16;
            ByteBuffer buffer = ByteBuffer.allocate(len);
            Hashing.xxhash128(buffer, key1);
            Hashing.xxhash128(buffer, key2);
            return new ByteData(buffer.flip().array());
        }

        @Override
        public DotAmount apply(ByteData result) {
            return new ScaleCodecReader(result.getBytes()).read(BalanceReader.INSTANCE);
        }
    }

    public static class AddressBalance implements StorageRequest<AccountInfo> {

        private final Address address;

        public AddressBalance(Address address) {
            this.address = address;
        }

        @Override
        public ByteData requestData() {
            String key1 = "System";
            String key2 = "Account";
            int len = 16 + 16 + 16 + 32;
            ByteBuffer buffer = ByteBuffer.allocate(len);
            Hashing.xxhash128(buffer, key1);
            Hashing.xxhash128(buffer, key2);
            Hashing.blake2128(buffer, address);
            buffer.put(address.getPubkey());
            return new ByteData(buffer.flip().array());
        }

        @Override
        public AccountInfo apply(ByteData result) {
            if (result == null) {
                return null;
            }
            return new ScaleCodecReader(result.getBytes()).read(new AccountInfoReader());
        }
    }

}
