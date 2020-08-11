package io.emeraldpay.polkaj.api;

public class PolkadotMethod {

    /**
     * Get list of available methods.
     * <br>
     * RPC Method: rpc_methods
     */
    public static final String RPC_METHODS = "rpc_methods";

    /**
     * Get header and body of a relay chain block
     * <br>
     * RPC Method: chain_getBlock
     */
    public static final String CHAIN_GET_BLOCK = "chain_getBlock";

    /**
     * Get the block hash for a specific block
     * <br>
     * RPC Method: chain_getBlockHash <br>
     * Alias: chain_getHead
     */
    public static final String CHAIN_GET_BLOCK_HASH = "chain_getBlockHash";

    /**
     * Get hash of the last finalized block in the canon chain
     * <br>
     * RPC Method: chain_getFinalizedHead <br>
     * Alias: chain_getFinalisedHead
     */
    public static final String CHAIN_GET_FINALIZED_HEAD = "chain_getFinalizedHead";

    /**
     * Retrieves the header for a specific block
     * <br>
     * RPC Method: chain_getHeader
     */
    public static final String CHAIN_GET_HEADER = "chain_getHeader";

    /**
     * Retrieves the newest header via subscription
     * <br>
     * RPC Method: chain_subscribeAllHeads <br>
     * Alias: subscribe_newHead, chain_subscribeNewHead
     *
     * @see #CHAIN_UNSUBSCRIBE_ALL_HEADS
     */
    public static final String CHAIN_SUBSCRIBE_ALL_HEADS = "chain_subscribeAllHeads";

    /**
     * Cancel subscription
     * <br>
     * RPC Method: chain_unsubscribeAllHeads
     *
     * @see #CHAIN_SUBSCRIBE_ALL_HEADS
     */
    public static final String CHAIN_UNSUBSCRIBE_ALL_HEADS = "chain_unsubscribeAllHeads";

    /**
     * Retrieves the best finalized header via subscription
     * <br>
     * RPC Method: chain_subscribeFinalizedHeads <br>
     * Alias: chain_subscribeFinalisedHeads
     *
     * @see #CHAIN_UNSUBSCRIBE_FINALIZED_HEADS
     */
    public static final String CHAIN_SUBSCRIBE_FINALIZED_HEADS = "chain_subscribeFinalizedHeads";

    /**
     * Cancel subscription
     * <br>
     * RPC Method: chain_unsubscribeFinalizedHeads
     *
     * @see #CHAIN_SUBSCRIBE_FINALIZED_HEADS
     */
    public static final String CHAIN_UNSUBSCRIBE_FINALIZED_HEADS = "chain_unsubscribeFinalizedHeads";

    /**
     * Retrieves the best header via subscription
     * <br>
     * RPC Method: chain_subscribeNewHeads <br>
     * Alias: subscribe_newHead, chain_subscribeNewHead
     *
     * @see #CHAIN_UNSUBSCRIBE_NEW_HEADS
     */
    public static final String CHAIN_SUBSCRIBE_NEW_HEADS = "chain_subscribeNewHeads";

    /**
     * Cancel subscription
     * <br>
     * RPC Method: chain_unsubscribeNewHeads
     *
     * @see #CHAIN_SUBSCRIBE_NEW_HEADS
     */
    public static final String CHAIN_UNSUBSCRIBE_NEW_HEADS = "chain_unsubscribeNewHeads";

    // ----- STATE ----

    /**
     * Perform a call to a builtin on the chain
     * <br>
     * RPC Method: state_call <br>
     * Alias: state_callAt
     */
    public static final String STATE_CALL = "state_call";

    /**
     * Returns the keys with prefix, leave empty to get all the keys
     * <br>
     * RPC Method: state_getPairs
     */
    public static final String STATE_GET_PAIRS = "state_getPairs";

    /**
     * Returns the keys with prefix with pagination support
     * <br>
     * RPC Method: state_getKeysPaged <br>
     * Alias: state_getKeysPagedAt, state_getKeys
     */
    public static final String STATE_KEYS_PAGED = "state_getKeysPaged";

    /**
     * Returns a storage entry at a specific block's state. <br>
     * RPC Method: state_getStorage <br>
     * Alias: state_getStorageAt
     */
    public static final String STATE_GET_STORAGE = "state_getStorage";

    /**
     * Returns the hash of a storage entry at a block's state. <br>
     * RPC Method: state_getStorageHash <br>
     * Alias: state_getStorageHashAt
     */
    public static final String STATE_GET_STORAGE_HASH = "state_getStorageHash";

    /**
     * Returns the size of a storage entry at a block's state. <br>
     * RPC Method: state_getStorageSize <br>
     * Alias: state_getStorageSizeAt
     */
    public static final String STATE_GET_STORAGE_SIZE = "state_getStorageSize";

    /**
     * Returns the runtime metadata as an opaque blob. <br>
     * RPC Method: state_getMetadata
     */
    public static final String STATE_GET_METADATA = "state_getMetadata";

    /**
     * Get the runtime version. <br>
     * RPC Method: state_getRuntimeVersion <br>
     * Alias: chain_getRuntimeVersion
     */
    public static final String STATE_GET_RUNTIME_VERSION = "state_getRuntimeVersion";

    /**
     * Query historical storage entries (by key) starting from a start block <br>
     * RPC Method: state_queryStorage
     */
    public static final String STATE_QUERY_STORAGE = "state_queryStorage";

    /**
     * Query storage entries (by key) starting at block hash given as the second parameter <br>
     * RPC Method: state_queryStorageAt
     */
    public static final String STATE_QUERY_STORAGE_AT = "state_queryStorageAt";

    /**
     * Returns proof of storage entries at a specific block's state. <br>
     * RPC Method: state_getReadProof
     */
    public static final String STATE_GET_READ_PROOF = "state_getReadProof";

    /**
     * New runtime version subscription <br>
     * RPC Method: state_subscribeRuntimeVersion <br>
     * Alias: chain_subscribeRuntimeVersion
     *
     * @see #STATE_UNSUBSCRIBE_RUNTIME_VERSION
     */
    public static final String STATE_SUBSCRIBE_RUNTIME_VERSION = "state_subscribeRuntimeVersion";

    /**
     * Cancel subscription <br>
     * RPC Method: state_subscribeRuntimeVersion
     *
     * @see #STATE_SUBSCRIBE_RUNTIME_VERSION
     */
    public static final String STATE_UNSUBSCRIBE_RUNTIME_VERSION = "state_unsubscribeRuntimeVersion";

    /**
     * New storage subscription <br>
     * RPC Method: state_subscribeStorage
     *
     * @see #STATE_UNSUBSCRIBE_STORAGE
     */
    public static final String STATE_SUBSCRIBE_STORAGE = "state_subscribeStorage";

    /**
     * Cancel subscription <br>
     * RPC Method: state_unsubscribeStorage
     *
     * @see #STATE_SUBSCRIBE_STORAGE
     */
    public static final String STATE_UNSUBSCRIBE_STORAGE = "state_unsubscribeStorage";

    // ----- SYSTEM ----

    /**
     * Get the node's implementation name.
     */
    public static final String SYSTEM_NAME = "system_name";

    /**
     * Get the node implementation's version. Should be a semver string.
     */
    public static final String SYSTEM_VERSION = "system_version";

    /**
     * Get the chain's name. Given as a string identifier.
     */
    public static final String SYSTEM_CHAIN = "system_chain";

    /**
     * Get the chain's type.
     */
    public static final String SYSTEM_CHAIN_TYPE = "system_chainType";

    /**
     * Get a custom set of properties as a JSON object, defined in the chain spec.
     */
    public static final String SYSTEM_PROPERTIES = "system_properties";

    /**
     * Return health status of the node.
     * <br>
     * Node is considered healthy if it is:
     * <ol>
     *     <li>connected to some peers (unless running in dev mode)</li>
     *     <li>not performing a major sync</li>
     * </ol>
     */
    public static final String SYSTEM_HEALTH = "system_health";

    /**
     * Returns the base58-encoded PeerId of the node.
     */
    public static final String SYSTEM_LOCAL_PEER_ID = "system_localPeerId";

    /**
     * Returns the multiaddresses that the local node is listening on
     */
    public static final String SYSTEM_LOCAL_LISTEN_ADDRESSES = "system_localListenAddresses";

    /**
     * Returns currently connected peers
     */
    public static final String SYSTEM_PEERS = "system_peers";

    /**
     * Adds a reserved peer. Returns the empty string or an error. The string
     * parameter should encode a `p2p` multiaddr.
     */
    public static final String SYSTEM_ADD_RESERVED_PEER = "system_addReservedPeer";

    /**
     * Remove a reserved peer. Returns the empty string or an error. The string
     * should encode only the PeerId e.g. <code>QmSk5HQbn6LhUwDiNMseVUjuRYhEtYj4aUZ6WfWoGURpdV</code>
     */
    public static final String SYSTEM_REMOVE_RESERVED_PEER = "system_removeReservedPeer";

    /**
     * Returns the roles the node is running as.
     */
    public static final String SYSTEM_NODE_ROLES = "system_nodeRoles";

    // ----- AUTHOR ----

    /**
     * Returns all pending extrinsics
     */
    public static final String AUTHOR_PENDING_EXTRINSICS = "author_pendingExtrinsics";

    /**
     * Submit a fully formatted extrinsic for block inclusion
     *
     */
    public static final String AUTHOR_SUBMIT_EXTRINSIC = "author_submitExtrinsic";

    /**
     * Remove given extrinsic from the pool and temporarily ban it to prevent reimporting
     */
    public static final String AUTHOR_REMOVE_EXTRINSIC = "author_removeExtrinsic";
}
