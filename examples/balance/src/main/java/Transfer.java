import io.emeraldpay.polkaj.api.PolkadotMethod;
import io.emeraldpay.polkaj.api.RpcCall;
import io.emeraldpay.polkaj.api.StandardCommands;
import io.emeraldpay.polkaj.apiws.PolkadotWsApi;
import io.emeraldpay.polkaj.json.RuntimeVersionJson;
import io.emeraldpay.polkaj.scale.ScaleExtract;
import io.emeraldpay.polkaj.scaletypes.AccountInfo;
import io.emeraldpay.polkaj.scaletypes.Metadata;
import io.emeraldpay.polkaj.scaletypes.MetadataReader;
import io.emeraldpay.polkaj.schnorrkel.Schnorrkel;
import io.emeraldpay.polkaj.ss58.SS58Type;
import io.emeraldpay.polkaj.tx.AccountRequests;
import io.emeraldpay.polkaj.tx.ExtrinsicContext;
import io.emeraldpay.polkaj.types.*;
import org.apache.commons.codec.binary.Hex;

import java.util.Random;

public class Transfer {

    public static void main(String[] args) throws Exception {
        String api = "ws://localhost:9944";
        if (args.length >= 1) {
            api = args[0];
        }
        System.out.println("Connect to: " + api);

        Schnorrkel.KeyPair aliceKey;
        Address alice;
        Address bob;
        if (args.length >= 3) {
            System.out.println("Use provided addresses");
            aliceKey = Schnorrkel.generateKeyPairFromSeed(Hex.decodeHex(args[1]));
            bob =  Address.from(args[2]);
        } else {
            System.out.println("Use standard accounts for Alice and Bob, expected to run against development network");
            aliceKey = Schnorrkel.generateKeyPairFromSeed(
                    Hex.decodeHex("e5be9a5092b81bca64be81d212e7f2f9eba183bb7a90954f7b76361f6edb5c0a")
            );
            bob =  Address.from("5FHneW46xGXgs5mUiveU4sbTyGBzmstUspZC92UhjJM694ty");
        }
        alice = new Address(SS58Type.Network.CANARY, aliceKey.getPublicKey());

        Random random = new Random();
        DotAmount amount = DotAmount.fromPlancks(
                Math.abs(random.nextLong()) % DotAmount.fromDots(0.002).getValue().longValue()
        );

        try (PolkadotWsApi client = PolkadotWsApi.newBuilder().connectTo(api).build()) {

            System.out.println("Connected: " + client.connect().get());

            RuntimeVersionJson runtimeVersion = client.execute(
                    StandardCommands.getInstance().getRuntimeVersion()
            ).get();
            System.out.println("Using runtime : " + runtimeVersion.getTransactionVersion() + ", " + runtimeVersion.getSpecVersion());

            Metadata metadata = client.execute(
                        StandardCommands.getInstance().stateMetadata()
                    )
                    .thenApply(ByteData::getBytes)
                    .thenApply(ScaleExtract.fromBytes(new MetadataReader()))
                    .get();

            Hash256 genesis = client.execute(
                    StandardCommands.getInstance().getBlockHash(0)
            ).get();
            System.out.println("Using genesis : " + genesis);

            AccountRequests.AddressBalance requestAccount = AccountRequests.balanceOf(alice);
            AccountInfo accountInfo = client.execute(
                    RpcCall.create(ByteData.class, PolkadotMethod.STATE_GET_STORAGE, requestAccount.requestData())
            ).thenApply(requestAccount).get();

            long nonce = accountInfo.getNonce();
            System.out.println("Currently available: " + DotAmountFormatter.autoFormatter().format(accountInfo.getData().getFree()));
            System.out.println("Use nonce  : " + nonce);
            System.out.println("Transfer " + DotAmountFormatter.autoFormatter().format(amount) + " from " + alice + " to " + bob);

            // prepare context for execution
            ExtrinsicContext context = ExtrinsicContext.newBuilder()
                    .nonce(nonce)
                    .genesis(genesis)
                    .runtime(runtimeVersion)
                    .build();

            // prepare call, and sign with sender Secret Key within the context
            AccountRequests.Transfer transfer = AccountRequests.transfer()
                    .runtime(metadata)
                    .from(alice)
                    .to(bob)
                    .amount(amount)
                    .sign(aliceKey, context)
                    .build();

            ByteData req = transfer.requestData();
            System.out.println("RPC Request Payload: " + req);
            Hash256 txid = client.execute(
                    StandardCommands.getInstance().authorSubmitExtrinsic(req)
            ).get();
            System.out.println("Tx Hash: " + txid);
        }
    }
}
