package io.emeraldpay.polkaj.tx;

import io.emeraldpay.polkaj.scale.ScaleCodecWriter;
import io.emeraldpay.polkaj.scale.ScaleWriter;
import io.emeraldpay.polkaj.scaletypes.BalanceTransfer;
import io.emeraldpay.polkaj.scaletypes.BalanceTransferWriter;
import io.emeraldpay.polkaj.scaletypes.ExtrinsicCall;
import io.emeraldpay.polkaj.schnorrkel.Schnorrkel;
import io.emeraldpay.polkaj.schnorrkel.SchnorrkelException;
import io.emeraldpay.polkaj.types.Address;
import io.emeraldpay.polkaj.types.Hash512;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class Signer {

    public static byte[] getPayload(ExtrinsicContext ctx, BalanceTransfer call) throws SignException {
        SignaturePayloadWriter<BalanceTransfer> codec = new SignaturePayloadWriter<>(new BalanceTransferWriter());
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try (ScaleCodecWriter writer = new ScaleCodecWriter(result)) {
            writer.write(codec, new SignaturePayload<>(ctx, call));
        } catch (IOException e) {
            throw new SignException("Failed to encode signature payload", e);
        }
        byte[] bytes = result.toByteArray();
        if (bytes.length > 256) {
            return Hashing.blake2(bytes);
        } else {
            return bytes;
        }
    }

    public static Hash512 sign(ExtrinsicContext ctx, BalanceTransfer call, Schnorrkel.KeyPair key) throws SignException {
        byte[] payload = getPayload(ctx, call);
        try {
            return new Hash512(Schnorrkel.sign(payload, key));
        } catch (SchnorrkelException e) {
            throw new SignException("Failed to sign", e);
        }
    }

    public static boolean isValid(ExtrinsicContext ctx, BalanceTransfer call, Hash512 signature, Address address) throws SignException {
        byte[] payload = getPayload(ctx, call);
        try {
            return Schnorrkel.verify(signature.getBytes(), payload, new Schnorrkel.PublicKey(address.getPubkey()));
        } catch (SchnorrkelException e) {
            throw new SignException("Failed to verify", e);
        }
    }

    public static class SignaturePayload<CALL extends ExtrinsicCall> {
        private final ExtrinsicContext context;
        private final CALL call;

        public SignaturePayload(ExtrinsicContext context, CALL call) {
            this.context = context;
            this.call = call;
        }

        public ExtrinsicContext getContext() {
            return context;
        }

        public CALL getCall() {
            return call;
        }
    }

    public static class SignaturePayloadWriter<CALL extends ExtrinsicCall> implements ScaleWriter<SignaturePayload<CALL>> {

        //TODO figure it out
        public static final int MAGIC_NUMBER = 6;

        private final ScaleWriter<CALL> callScaleWriter;

        public SignaturePayloadWriter(ScaleWriter<CALL> callScaleWriter) {
            this.callScaleWriter = callScaleWriter;
        }

        @Override
        public void write(ScaleCodecWriter wrt, SignaturePayload<CALL> signPayload) throws IOException {
            ExtrinsicContext context = signPayload.getContext();
            wrt.writeByte(MAGIC_NUMBER);
            wrt.write(callScaleWriter, signPayload.getCall());
            wrt.write(ScaleCodecWriter.COMPACT_BIGINT, BigInteger.valueOf(context.getEra().birth(context.getEraHeight())));
            wrt.write(ScaleCodecWriter.COMPACT_BIGINT, BigInteger.valueOf(context.getNonce()));
            wrt.write(ScaleCodecWriter.COMPACT_BIGINT, context.getTip().getValue());
            wrt.writeUint32(context.getRuntimeVersion());
            wrt.writeUint32(context.getTxVersion());
            wrt.writeUint256(context.getGenesis().getBytes());
            if (context.getEra().isImmortal()) {
                wrt.writeUint256(context.getGenesis().getBytes());
            } else {
                wrt.writeUint256(context.getEraBlockHash().getBytes());
            }
        }
    }
}
