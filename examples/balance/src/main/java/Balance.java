import io.emeraldpay.polkaj.api.PolkadotApi;
import io.emeraldpay.polkaj.api.RpcCallAdapter;
import io.emeraldpay.polkaj.apihttp.JavaHttpAdapter;
import io.emeraldpay.polkaj.apiokhttp.OkHttpRpcAdapter;
import io.emeraldpay.polkaj.scaletypes.AccountInfo;
import io.emeraldpay.polkaj.tx.AccountRequests;
import io.emeraldpay.polkaj.types.Address;
import io.emeraldpay.polkaj.types.DotAmount;
import io.emeraldpay.polkaj.types.DotAmountFormatter;

import java.util.Arrays;

public class Balance {

    public static void main(String[] args) throws Exception {
        final boolean useOkhttp = Arrays.asList(args).contains("okhttp");
        final RpcCallAdapter adapter = useOkhttp ? OkHttpRpcAdapter.newBuilder().build() :
                JavaHttpAdapter.newBuilder().build();
        try (PolkadotApi client = PolkadotApi.newBuilder().rpcCallAdapter(adapter).build()) {
            DotAmountFormatter formatter = DotAmountFormatter.autoFormatter();

            DotAmount total = AccountRequests.totalIssuance().execute(client).get();
            System.out.println(
                    "Total Issued: " +
                            formatter.format(total)
            );

            Address address = Address.from("5C7f75bEAaDkpMAW12S9rPraTmWc7U36jS9rBkYvYugygD2C");
            System.out.println("Address: " + address);

            AccountInfo balance = AccountRequests.balanceOf(address).execute(client).get();
            if (balance == null) {
                System.out.println("NO BALANCE");
                return;
            }

            StringBuilder status = new StringBuilder();
            status
                    .append("Balance: ")
                    .append(formatter.format(balance.getData().getFree()));

            if (!balance.getData().getFeeFrozen().equals(DotAmount.ZERO)
                    || !balance.getData().getMiscFrozen().equals(DotAmount.ZERO)) {
                status.append(" (frozen ")
                        .append(formatter.format(balance.getData().getFeeFrozen()))
                        .append(" for Fee, frozen ")
                        .append(formatter.format(balance.getData().getMiscFrozen()))
                        .append(" for Misc.)");
            }

            System.out.println(status.toString());
        }
    }
}
