package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.scale.ScaleCodecWriter;
import io.emeraldpay.polkaj.scale.ScaleWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;

public class ExtrinsicWriter<CALL extends ExtrinsicCall> implements ScaleWriter<Extrinsic<CALL>> {

    private static final TransactionInfoWriter TX_WRITER = new TransactionInfoWriter();

    private final ScaleWriter<CALL> callScaleWriter;

    public ExtrinsicWriter(ScaleWriter<CALL> callScaleWriter) {
        this.callScaleWriter = callScaleWriter;
    }

    @Override
    public void write(ScaleCodecWriter wrt, Extrinsic<CALL> value) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        ScaleCodecWriter internal = new ScaleCodecWriter(buf);
        int type = Extrinsic.TYPE_BIT_SIGNED + (Extrinsic.TYPE_UNMASK_VERSION & 4);
        internal.writeByte(type);
        internal.write(TX_WRITER, value.getTx());
        internal.write(callScaleWriter, value.getCall());
        // the extrinsic itself is written as array, so the body of it can be processed individually as bytes
        wrt.writeAsList(buf.toByteArray());
    }

    static class TransactionInfoWriter implements ScaleWriter<Extrinsic.TransactionInfo> {

        private static final EraWriter ERA_WRITER = new EraWriter();

        @Override
        public void write(ScaleCodecWriter wrt, Extrinsic.TransactionInfo value) throws IOException {
            wrt.writeUint256(value.getSender().getPubkey());
            wrt.writeByte(Extrinsic.SignatureType.SR25519.getCode());
            wrt.writeByteArray(value.getSignature().getValue().getBytes());
            wrt.write(ERA_WRITER, value.getEra());
            wrt.write(ScaleCodecWriter.COMPACT_BIGINT, BigInteger.valueOf(value.getNonce()));
            wrt.write(ScaleCodecWriter.COMPACT_BIGINT, value.getTip().getValue());
        }
    }
}
