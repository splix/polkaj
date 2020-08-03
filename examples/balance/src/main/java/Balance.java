import io.emeraldpay.polkaj.api.PolkadotMethod;
import io.emeraldpay.polkaj.api.RpcCall;
import io.emeraldpay.polkaj.apihttp.PolkadotHttpApi;
import io.emeraldpay.polkaj.scaletypes.AccountInfo;
import io.emeraldpay.polkaj.tx.AccountRequests;
import io.emeraldpay.polkaj.types.Address;
import io.emeraldpay.polkaj.types.ByteData;
import io.emeraldpay.polkaj.types.DotAmount;
import io.emeraldpay.polkaj.types.DotAmountFormatter;

public class Balance {

    public static void main(String[] args) throws Exception {
        try (PolkadotHttpApi client = PolkadotHttpApi.newBuilder().build()) {
            DotAmountFormatter formatter = DotAmountFormatter.autoFormatter();

            AccountRequests.TotalIssuance reqIssuance = AccountRequests.totalIssuance();

            DotAmount total = client.execute(
                    RpcCall.create(ByteData.class, PolkadotMethod.STATE_GET_STORAGE, reqIssuance.requestData())
            ).thenApply(reqIssuance).get();


            System.out.println(
                    "Total Issued: " +
                            formatter.format(total)
            );

            Address address = Address.from("5C7f75bEAaDkpMAW12S9rPraTmWc7U36jS9rBkYvYugygD2C");
            System.out.println("Address: " + address);
            AccountRequests.AddressBalance reqAddress = AccountRequests.balanceOf(address);

            AccountInfo balance = client.execute(
                    RpcCall.create(ByteData.class, PolkadotMethod.STATE_GET_STORAGE, reqAddress.requestData())
            ).thenApply(reqAddress).get();

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
