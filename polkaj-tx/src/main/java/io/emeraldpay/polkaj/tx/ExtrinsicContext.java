package io.emeraldpay.polkaj.tx;

import io.emeraldpay.polkaj.json.RuntimeVersionJson;
import io.emeraldpay.polkaj.types.DotAmount;
import io.emeraldpay.polkaj.types.Hash256;

import java.util.Objects;

/**
 * Context to execute an Extrinsic
 */
public class ExtrinsicContext {

    /**
     * Transaction version supported by target runtime
     */
    private int txVersion;

    /**
     * Target runtime version
     */
    private int runtimeVersion;

    /**
     * Genesis block hash
     */
    private Hash256 genesis;

    /**
     * Block hash of the current era for Mortal transaction, or genesis hash for Immortal
     * @see Era
     */
    private Hash256 eraBlockHash;

    /**
     * Sender nonce
     */
    private long nonce = 0;

    /**
     * Era to execute, if mortal transaction. Default: Immortal
     */
    private Era era = Era.IMMORTAL;

    /**
     * Validator tip. Default: 0
     */
    private DotAmount tip = DotAmount.ZERO;

    /**
     * Era height, if mortal transaction.
     */
    private long eraHeight = 0;

    /**
     * Start new builder for the context
     *
     * @return builder
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    public int getTxVersion() {
        return txVersion;
    }

    public void setTxVersion(int txVersion) {
        this.txVersion = txVersion;
    }

    public int getRuntimeVersion() {
        return runtimeVersion;
    }

    public void setRuntimeVersion(int runtimeVersion) {
        this.runtimeVersion = runtimeVersion;
    }

    public Hash256 getGenesis() {
        return genesis;
    }

    public void setGenesis(Hash256 genesis) {
        this.genesis = genesis;
    }

    public Hash256 getEraBlockHash() {
        return eraBlockHash;
    }

    public void setEraBlockHash(Hash256 eraBlockHash) {
        this.eraBlockHash = eraBlockHash;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public Era getEra() {
        return era;
    }

    public void setEra(Era era) {
        this.era = era;
    }

    public DotAmount getTip() {
        return tip;
    }

    public void setTip(DotAmount tip) {
        this.tip = tip;
    }

    public long getEraHeight() {
        return eraHeight;
    }

    public void setEraHeight(long eraHeight) {
        this.eraHeight = eraHeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExtrinsicContext)) return false;
        ExtrinsicContext context = (ExtrinsicContext) o;
        return txVersion == context.txVersion &&
                runtimeVersion == context.runtimeVersion &&
                nonce == context.nonce &&
                eraHeight == context.eraHeight &&
                Objects.equals(genesis, context.genesis) &&
                Objects.equals(eraBlockHash, context.eraBlockHash) &&
                Objects.equals(era, context.era) &&
                Objects.equals(tip, context.tip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(txVersion, runtimeVersion, genesis, eraBlockHash, nonce, era, tip, eraHeight);
    }

    public static final class Builder {
        private int txVersion = 1;
        private int runtimeVersion = 254;
        private Hash256 genesis = Hash256.empty();
        private Hash256 eraBlockHash = Hash256.empty();

        private long nonce = 0;
        private Era era = Era.IMMORTAL;
        private DotAmount tip = DotAmount.ZERO;
        private long eraHeight = 0;

        public Builder runtime(RuntimeVersionJson version) {
            return runtime(version.getTransactionVersion(), version.getSpecVersion());
        }

        public Builder runtime(int txVersion, int runtimeVersion) {
            this.txVersion = txVersion;
            this.runtimeVersion = runtimeVersion;
            return this;
        }

        public Builder genesis(Hash256 genesis) {
            this.genesis = genesis;
            if (era.isImmortal()) {
                this.eraBlockHash = genesis;
            }
            return this;
        }

        public Builder nonce(long nonce) {
            this.nonce = nonce;
            return this;
        }

        public Builder tip(DotAmount amount) {
            this.tip = amount;
            return this;
        }

        public ExtrinsicContext build() {
            ExtrinsicContext context = new ExtrinsicContext();
            context.setTxVersion(txVersion);
            context.setRuntimeVersion(runtimeVersion);
            context.setGenesis(genesis);
            context.setEraBlockHash(eraBlockHash);
            context.setNonce(nonce);
            context.setEra(era);
            context.setTip(tip);
            context.setEraHeight(eraHeight);
            return context;
        }
    }
}
