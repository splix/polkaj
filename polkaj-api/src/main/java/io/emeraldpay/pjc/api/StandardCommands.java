package io.emeraldpay.pjc.api;

import io.emeraldpay.pjc.json.BlockResponseJson;
import io.emeraldpay.pjc.json.MethodsJson;
import io.emeraldpay.pjc.json.SystemHealthJson;
import io.emeraldpay.pjc.types.Hash256;

import java.util.List;

/**
 * Standard/common Polkadot Commands
 */
public class StandardCommands {

    private static final StandardCommands instance = new StandardCommands();

    public static StandardCommands getInstance() {
        return instance;
    }

    /**
     * Request for a block by its hash
     * @param hash hash of the block
     * @return command
     */
    public RpcCall<BlockResponseJson> getBlock(Hash256 hash) {
        return RpcCall.create(BlockResponseJson.class, "chain_getBlock", hash);
    }

    /**
     * Request for the hash of the current finalized head
     * @return command
     */
    public RpcCall<Hash256> getFinalizedHead() {
        return RpcCall.create(Hash256.class, "chain_getFinalizedHead");
    }

    /**
     * Request a list of available RPC methods
     * @return command
     */
    public RpcCall<MethodsJson> methods() {
        return RpcCall.create(MethodsJson.class, "rpc_methods");
    }

    /**
     * Request name of the current chain
     * @return command
     */
    public RpcCall<String> systemChain() {
        return RpcCall.create(String.class, "system_chain");
    }

    /**
     * Request health status of the current node
     * @return command
     */
    public RpcCall<SystemHealthJson> systemHealth() {
        return RpcCall.create(SystemHealthJson.class, "system_health");
    }

    /**
     * Request name of the current node
     * @return command
     */
    public RpcCall<String> systemName() {
        return RpcCall.create(String.class, "system_name");
    }

    /**
     * Request roles of the current node
     * @return command
     */
    public RpcCall<List<String>> systemNodeRoles() {
        return RpcCall.create(String.class, "system_nodeRoles").expectList();
    }
}
