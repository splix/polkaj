package io.emeraldpay.polkaj.api;

import io.emeraldpay.polkaj.json.*;
import io.emeraldpay.polkaj.types.ByteData;
import io.emeraldpay.polkaj.types.Hash256;

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
     * Request for the hash of the current head
     * @return command
     */
    public RpcCall<Hash256> getHead() {
        return RpcCall.create(Hash256.class, "chain_getHead");
    }

    /**
     * Request the runtime version of the blockchain
     * @return command
     */
    public RpcCall<RuntimeVersionJson> getRuntimeVersion() {
        return RpcCall.create(RuntimeVersionJson.class, "chain_getRuntimeVersion");
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

    /**
     * Request peer list connected to the current node
     * @return command
     */
    public RpcCall<List<PeerJson>> systemPeers() {
        return RpcCall.create(PeerJson.class, "system_peers").expectList();
    }

    /**
     * Request version of the current node
     * @return command
     */
    public RpcCall<String> systemVersion() {
        return RpcCall.create(String.class, "system_version");
    }

    /**
     * Request runtime metadata of the current node
     * @return command
     */
    public RpcCall<ByteData> stateMetadata() {
        return RpcCall.create(ByteData.class, "state_getMetadata");
    }

    /**
     * Request data from storage
     * @param request key (depending on the storage)
     * @return command
     */
    public RpcCall<ByteData> stateGetStorage(ByteData request) {
        return RpcCall.create(ByteData.class, "state_getStorage", request.toString());
    }

    /**
     * Request data from storage
     * @param request key (depending on the storage)
     * @return command
     */
    public RpcCall<ByteData> stateGetStorage(byte[] request) {
        return stateGetStorage(new ByteData(request));
    }

    public RpcCall<ContractExecResultJson> contractsCall(ContractCallRequestJson request) {
        return RpcCall.create(ContractExecResultJson.class, "contracts_call", request);
    }

    public RpcCall<ContractExecResultJson> contractsCall(ContractCallRequestJson request, Hash256 blockHash) {
        return RpcCall.create(ContractExecResultJson.class, "contracts_call", request, blockHash);
    }
}
