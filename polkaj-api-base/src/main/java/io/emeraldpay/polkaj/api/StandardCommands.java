package io.emeraldpay.polkaj.api;

import io.emeraldpay.polkaj.json.*;
import io.emeraldpay.polkaj.types.Address;
import io.emeraldpay.polkaj.types.ByteData;
import io.emeraldpay.polkaj.types.Hash256;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        return RpcCall.create(BlockResponseJson.class, PolkadotMethod.CHAIN_GET_BLOCK, hash);
    }

    public RpcCall<Hash256> getBlockHash() {
        return RpcCall.create(Hash256.class, PolkadotMethod.CHAIN_GET_BLOCK_HASH);
    }

    public RpcCall<Hash256> getBlockHash(long at) {
        return RpcCall.create(Hash256.class, PolkadotMethod.CHAIN_GET_BLOCK_HASH, at);
    }

    /**
     * Request for the hash of the current finalized head
     * @return command
     */
    public RpcCall<Hash256> getFinalizedHead() {
        return RpcCall.create(Hash256.class, PolkadotMethod.CHAIN_GET_FINALIZED_HEAD);
    }

    /**
     * Request the runtime version of the blockchain
     * @return command
     */
    public RpcCall<RuntimeVersionJson> getRuntimeVersion() {
        return RpcCall.create(RuntimeVersionJson.class, PolkadotMethod.STATE_GET_RUNTIME_VERSION);
    }

    /**
     * Request a list of available RPC methods
     * @return command
     */
    public RpcCall<MethodsJson> methods() {
        return RpcCall.create(MethodsJson.class, PolkadotMethod.RPC_METHODS);
    }

    /**
     * Request name of the current chain
     * @return command
     */
    public RpcCall<String> systemChain() {
        return RpcCall.create(String.class, PolkadotMethod.SYSTEM_CHAIN);
    }

    /**
     * Request health status of the current node
     * @return command
     */
    public RpcCall<SystemHealthJson> systemHealth() {
        return RpcCall.create(SystemHealthJson.class, PolkadotMethod.SYSTEM_HEALTH);
    }

    /**
     * Request name of the current node
     * @return command
     */
    public RpcCall<String> systemName() {
        return RpcCall.create(String.class, PolkadotMethod.SYSTEM_NAME);
    }

    /**
     * Request roles of the current node
     * @return command
     */
    public RpcCall<List<String>> systemNodeRoles() {
        return RpcCall.create(String.class, PolkadotMethod.SYSTEM_NODE_ROLES).expectList();
    }

    /**
     * Request peer list connected to the current node
     * @return command
     */
    public RpcCall<List<PeerJson>> systemPeers() {
        return RpcCall.create(PeerJson.class, PolkadotMethod.SYSTEM_PEERS).expectList();
    }

    /**
     * Request version of the current node
     * @return command
     */
    public RpcCall<String> systemVersion() {
        return RpcCall.create(String.class, PolkadotMethod.SYSTEM_VERSION);
    }

    /**
     * Request runtime metadata of the current node
     * @return command
     */
    public RpcCall<ByteData> stateMetadata() {
        return RpcCall.create(ByteData.class, PolkadotMethod.STATE_GET_METADATA);
    }

    /**
     * Request data from storage
     * @param key key (depending on the storage)
     * @return command
     */
    public RpcCall<ByteData> stateGetStorage(ByteData key) {
        return RpcCall.create(ByteData.class, PolkadotMethod.STATE_GET_STORAGE, key.toString());
    }

    public RpcCall<ReadProofJson> stateGetReadProof(List<ByteData> keys, Hash256 at) {
        List<Object> params = new ArrayList<>(Collections.unmodifiableList(keys));
        if (at != null) {
            params.add(at);
        }
        return RpcCall.create(ReadProofJson.class, PolkadotMethod.STATE_GET_READ_PROOF, params);
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

    public RpcCall<ContractExecResultJson> contractsCall(ContractCallRequestJson request, Hash256 at) {
        if (at == null) {
            return contractsCall(request);
        }
        return RpcCall.create(ContractExecResultJson.class, "contracts_call", request, at);
    }

    /**
     *
     * @param address contract address
     * @param key key
     * @return comman
     */
    public RpcCall<ByteData> contractsGetStorage(Address address, Hash256 key) {
        return RpcCall.create(ByteData.class, "contracts_getStorage", address, key);
    }

    /**
     *
     * @param address contract address
     * @param key key
     * @param at block hash
     * @return command
     */
    public RpcCall<ByteData> contractsGetStorage(Address address, Hash256 key, Hash256 at) {
        if (at == null) {
            return contractsGetStorage(address, key);
        }
        return RpcCall.create(ByteData.class, "contracts_getStorage", address, key, at);
    }

    /**
     *
     * @param address contract address
     * @return command
     */
    public RpcCall<Long> contractsRentProjection(Address address) {
        return RpcCall.create(Long.class, "contracts_rentProjection", address);
    }

    /**
     *
     * @param address contract address
     * @param at block hash
     * @return command
     */
    public RpcCall<Long> contractsRentProjection(Address address, Hash256 at) {
        if (at == null) {
            return contractsRentProjection(address);
        }
        return RpcCall.create(Long.class, "contracts_rentProjection", address, at);
    }

    /**
     * Returns all pending extrinsics
     *
     * @return command
     */
    public RpcCall<List<ByteData>> authorPendingExtrinsics() {
        return RpcCall.create(ByteData.class, PolkadotMethod.AUTHOR_PENDING_EXTRINSICS).expectList();
    }

    /**
     * Submit a fully formatted extrinsic for block inclusion
     *
     * @param extrinsic encoded extrinsic
     * @return command, returns hash of the submitted extrinsic
     */
    public RpcCall<Hash256> authorSubmitExtrinsic(ByteData extrinsic) {
        if (extrinsic == null) {
            throw new NullPointerException("Extrinsic cannot be null");
        }
        if (extrinsic.getBytes().length == 0) {
            throw new IllegalArgumentException("Empty extrinsic");
        }
        return RpcCall.create(Hash256.class, PolkadotMethod.AUTHOR_SUBMIT_EXTRINSIC, extrinsic);
    }


    /**
     * Remove given extrinsic(s) from the pool and temporarily ban it to prevent reimporting
     *
     * @param hash tx hash
     * @return command
     */
    public RpcCall<List<Hash256>> authorRemoveExtrinsic(Hash256 ... hash) {
        return RpcCall.create(Hash256.class, PolkadotMethod.AUTHOR_REMOVE_EXTRINSIC, Arrays.asList(hash)).expectList();
    }

    /**
     * Remove given extrinsic(s) from the pool and temporarily ban it to prevent reimporting
     *
     * @param extrinsic raw extrinsic to remove
     * @return command
     */
    public RpcCall<List<Hash256>> authorRemoveExtrinsic(ByteData ... extrinsic) {
        return RpcCall.create(Hash256.class, PolkadotMethod.AUTHOR_REMOVE_EXTRINSIC, Arrays.asList(extrinsic)).expectList();
    }

    /**
     * Returns true if the keystore has private keys for the given public key and key type.
     *
     * @param address public key
     * @param keyType key type
     * @return command
     */
    public RpcCall<Boolean> authorHasKey(Address address, String keyType) {
        return RpcCall.create(Boolean.class, PolkadotMethod.AUTHOR_HAS_KEY, new ByteData(address.getPubkey()), keyType);
    }

    /**
     * Returns true if the keystore has private keys for the given session public keys
     *
     * @param keys public keys
     * @return command
     */
    public RpcCall<Boolean> authorHasSessionKeys(ByteData keys) {
        return RpcCall.create(Boolean.class, PolkadotMethod.AUTHOR_HAS_SESSION_KEYS, keys);
    }

    /**
     * Insert a key into the keystore.
     *
     * @param keyType key type
     * @param suri SURI
     * @param address public key
     * @return command
     */
    public RpcCall<ByteData> authorInsertKey(String keyType, String suri, Address address) {
        return RpcCall.create(ByteData.class, PolkadotMethod.AUTHOR_INSERT_KEY, keyType, suri, new ByteData(address.getPubkey()));
    }

    /**
     * Generate new session keys and returns the corresponding public keys
     *
     * @return command
     */
    public RpcCall<ByteData> authorRotateKeys() {
        return RpcCall.create(ByteData.class, PolkadotMethod.AUTHOR_ROTATE_KEYS);
    }

    /**
     * Next index for the address
     *
     * @param address address
     * @return command
     */
    public RpcCall<Integer> accountNextIndex(Address address) {
        return RpcCall.create(Integer.class, PolkadotMethod.ACCOUNT_NEXT_INDEX, address);
    }

    /**
     * Retrieves the fee information for an encoded extrinsic
     *
     * @param extrinsic encoded extrinsic
     * @return command
     */
    public RpcCall<RuntimeDispatchInfoJson> paymentQueryInfo(ByteData extrinsic) {
        return RpcCall.create(RuntimeDispatchInfoJson.class, PolkadotMethod.PAYMENT_QUERY_INFO, extrinsic);
    }

    /**
     * Retrieves the fee information for an encoded extrinsic
     *
     * @param extrinsic encoded extrinsic
     * @param block target block
     * @return command
     */
    public RpcCall<RuntimeDispatchInfoJson> paymentQueryInfo(ByteData extrinsic, Hash256 block) {
        return RpcCall.create(RuntimeDispatchInfoJson.class, PolkadotMethod.PAYMENT_QUERY_INFO, extrinsic, block);
    }

}
