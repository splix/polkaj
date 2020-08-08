package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.types.Address;
import io.emeraldpay.polkaj.types.DotAmount;
import io.emeraldpay.polkaj.types.Hash512;

import java.util.Objects;

/**
 * Extrinsic Data, contains main details {@link #tx} with signature and other initial definitions, and the actual
 * call details {@link #call}
 *
 *
 * <ul>
 *     <li><a href="https://hackmd.io/@gavwood/r1jTRX2Zr">https://hackmd.io/@gavwood/r1jTRX2Zr</a></li>
 * </ul>
 *
 * @see ExtrinsicCall
 */
public class Extrinsic<CALL extends ExtrinsicCall> {

    public static final int TYPE_BIT_SIGNED = 0b10000000;
    public static final int TYPE_UNMASK_VERSION = 0b01111111;

    private TransactionInfo tx;
    private CALL call;

    public TransactionInfo getTx() {
        return tx;
    }

    public void setTx(TransactionInfo tx) {
        this.tx = tx;
    }

    public CALL getCall() {
        return call;
    }

    public void setCall(CALL call) {
        this.call = call;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Extrinsic)) return false;
        Extrinsic<?> extrinsic = (Extrinsic<?>) o;
        return Objects.equals(tx, extrinsic.tx) &&
                Objects.equals(call, extrinsic.call);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tx, call);
    }

    public static enum SignatureType {
        ED25519(0),
        SR25519(1),
        ECDSA(2);

        private final int code;

        SignatureType(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static SignatureType fromCode(int code) {
            for (SignatureType type: SignatureType.values()) {
                if (type.code == code) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown signature code: " + code);
        }

    }

    /**
     * Main details about Extrinsic
     */
    public static class TransactionInfo {
        /**
         * Sender
         */
        private Address sender;
        /**
         * Signature
         */
        private SR25519Signature signature;
        /**
         * Era to execute extrinsic. Immortal by default (i.e. 0)
         */
        private Integer era = 0;
        /**
         * Sender nonce
         */
        private Long nonce;
        /**
         * Tip to validator. Zero by default.
         */
        private DotAmount tip = DotAmount.ZERO;

        public Address getSender() {
            return sender;
        }

        public void setSender(Address sender) {
            this.sender = sender;
        }

        public SR25519Signature getSignature() {
            return signature;
        }

        public void setSignature(SR25519Signature signature) {
            this.signature = signature;
        }

        public Integer getEra() {
            return era;
        }

        public void setEra(Integer era) {
            this.era = era;
        }

        public Long getNonce() {
            return nonce;
        }

        public void setNonce(Long nonce) {
            this.nonce = nonce;
        }

        public DotAmount getTip() {
            return tip;
        }

        public void setTip(DotAmount tip) {
            this.tip = tip;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TransactionInfo)) return false;
            TransactionInfo that = (TransactionInfo) o;
            return Objects.equals(sender, that.sender) &&
                    Objects.equals(signature, that.signature) &&
                    Objects.equals(era, that.era) &&
                    Objects.equals(nonce, that.nonce) &&
                    Objects.equals(tip, that.tip);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sender, era, nonce);
        }
    }

    public static class SR25519Signature {
        private final Hash512 value;

        public SR25519Signature(Hash512 value) {
            this.value = value;
        }

        public Hash512 getValue() {
            return value;
        }

        public SignatureType getType() {
            return SignatureType.SR25519;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SR25519Signature)) return false;
            SR25519Signature that = (SR25519Signature) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

}
