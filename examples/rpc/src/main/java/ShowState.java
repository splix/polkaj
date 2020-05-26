import com.fasterxml.jackson.databind.ObjectMapper;
import io.emeraldpay.pjc.api.PolkadotApi;
import io.emeraldpay.pjc.api.RpcCall;
import io.emeraldpay.pjc.apihttp.PolkadotHttpApi;
import io.emeraldpay.pjc.json.BlockResponseJson;
import io.emeraldpay.pjc.json.jackson.PolkadotModule;
import io.emeraldpay.pjc.types.Hash256;

import java.net.URISyntaxException;
import java.util.concurrent.Future;

public class ShowState {
    public static void main(String[] args) throws Exception {
        PolkadotHttpApi client = PolkadotHttpApi.newBuilder().build();
        Future<Hash256> hashFuture = client.execute(
                // use RpcCall.create to define the request
                // the first parameter is Class / JavaType of the expected result
                // second is the method name
                // and optionally a list of parameters for the call
                RpcCall.create(Hash256.class, "chain_getFinalisedHead")
        );
        Hash256 hash;

        hash = hashFuture.get();
        System.out.println("Current head: " + hash);

        Future<BlockResponseJson> blockFuture = client.execute(
                // Another way to prepare a call, instead of manually constructing RpcCall instances
                // is to use standard commands provided by PolkadotApi.commands()
                // the following line is same as calling with
                // RpcCall.create(BlockResponseJson.class, "chain_getBlock", hash)
                PolkadotApi.commands().getBlock(hash)
        );
        BlockResponseJson block = blockFuture.get();
        System.out.println("Current height: " + block.getBlock().getHeader().getNumber());
        System.out.println("State hash: " + block.getBlock().getHeader().getStateRoot());
        client.close();
    }

    PolkadotApi client() throws URISyntaxException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new PolkadotModule());

        PolkadotHttpApi client = PolkadotHttpApi.newBuilder()
                .objectMapper(objectMapper) // <1>
                .connectTo("http://10.0.1.20:9333") // <2>
                .basicAuth("alice", "secret") // <3>
                .build();

        return client;
    }

}
