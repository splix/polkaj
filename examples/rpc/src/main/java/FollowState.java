import io.emeraldpay.polkaj.api.PolkadotSubscriptionApi;
import io.emeraldpay.polkaj.api.SubscribeCall;
import io.emeraldpay.polkaj.api.Subscription;
import io.emeraldpay.polkaj.apiws.PolkadotWsApi;
import io.emeraldpay.polkaj.json.BlockJson;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FollowState {

    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        PolkadotWsApi client = PolkadotWsApi.newBuilder().build();

        // IMPORTANT! connect to the node as the first step before making calls or subscriptions.
        client.connect().get(5, TimeUnit.SECONDS);

        Future<Subscription<BlockJson.Header>> hashFuture = client.subscribe(PolkadotSubscriptionApi.subscriptions().newHeads());

        Subscription<BlockJson.Header> subscription = hashFuture.get(5, TimeUnit.SECONDS);
        subscription.handler((Subscription.Event<BlockJson.Header> event) -> {
            BlockJson.Header header = event.getResult();
            List<String> line = List.of(
                    Instant.now().truncatedTo(ChronoUnit.SECONDS).toString(),
                    header.getNumber().toString(),
                    header.getStateRoot().toString()
            );
            System.out.println(String.join("\t", line));
        });

    }
}
