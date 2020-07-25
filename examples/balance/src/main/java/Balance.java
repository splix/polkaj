import io.emeraldpay.polkaj.api.PolkadotMethod;
import io.emeraldpay.polkaj.api.RpcCall;
import io.emeraldpay.polkaj.apihttp.PolkadotHttpApi;
import io.emeraldpay.polkaj.scaletypes.AccountInfo;
import io.emeraldpay.polkaj.tx.BalanceRequest;
import io.emeraldpay.polkaj.types.Address;
import io.emeraldpay.polkaj.types.ByteData;
import io.emeraldpay.polkaj.types.DotAmount;
import io.emeraldpay.polkaj.types.DotAmountFormatter;

public class Balance {

    public static void main(String[] args) throws Exception {
        PolkadotHttpApi client = PolkadotHttpApi.newBuilder().build();
        DotAmountFormatter formatter = DotAmountFormatter.autoFormatter();

        BalanceRequest.TotalIssuance reqIssuance = BalanceRequest.totalIssuance();

        DotAmount total = client.execute(
                RpcCall.create(ByteData.class, PolkadotMethod.STATE_GET_STORAGE, reqIssuance.requestData())
        ).thenApply(reqIssuance).get();


        System.out.println(
                "Total Issued: " +
                formatter.format(total)
        );

        Address address = Address.from("1WG3jyNqniQMRZGQUc7QD2kVLT8hkRPGMSqAb5XYQM1UDxN");
        System.out.println("Address: " + address);
        BalanceRequest.AddressBalance reqAddress = BalanceRequest.balanceOf(address);

        AccountInfo balance = client.execute(
                RpcCall.create(ByteData.class, PolkadotMethod.STATE_GET_STORAGE, reqAddress.requestData())
        ).thenApply(reqAddress).get();

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

        client.close();
    }
}
