import io.emeraldpay.pjc.apiws.PolkadotWsApi;
import io.emeraldpay.pjc.apiws.Subscription;
import io.emeraldpay.pjc.json.BlockJson;

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
        client.connect().get(5, TimeUnit.SECONDS);
        Future<Subscription<BlockJson.Header>> hashFuture = client.subscribe(BlockJson.Header.class, "chain_subscribeNewHead", "chain_unsubscribeNewHead");
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
