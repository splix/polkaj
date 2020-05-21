import com.fasterxml.jackson.databind.ObjectMapper;
import io.emeraldpay.pjc.clientrpc.PolkadotRpcClient;
import io.emeraldpay.pjc.json.BlockResponseJson;
import io.emeraldpay.pjc.json.jackson.PolkadotModule;
import io.emeraldpay.pjc.types.Hash256;

import java.net.URISyntaxException;
import java.util.concurrent.Future;

public class ShowState {
    public static void main(String[] args) throws Exception {
        PolkadotRpcClient client = PolkadotRpcClient.newBuilder().build();
        Future<Hash256> hashFuture = client.execute(Hash256.class, "chain_getFinalisedHead");
        Hash256 hash;

        hash = hashFuture.get();
        System.out.println("Current head: " + hash);

        Future<BlockResponseJson> blockFuture = client.execute(BlockResponseJson.class, "chain_getBlock", hash);
        BlockResponseJson block = blockFuture.get();
        System.out.println("Current height: " + block.getBlock().getHeader().getNumber());
        System.out.println("State hash: " + block.getBlock().getHeader().getStateRoot());
    }

    PolkadotRpcClient client() throws URISyntaxException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new PolkadotModule());

        PolkadotRpcClient client = PolkadotRpcClient.newBuilder()
                .objectMapper(objectMapper) // <1>
                .connectTo("http://10.0.1.20:9333") // <2>
                .basicAuth("alice", "secret") // <3>
                .build();

        return client;
    }

}
