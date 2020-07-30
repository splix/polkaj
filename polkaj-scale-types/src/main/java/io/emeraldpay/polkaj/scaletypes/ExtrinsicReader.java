package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.scale.ScaleCodecReader;
import io.emeraldpay.polkaj.scale.ScaleReader;
import io.emeraldpay.polkaj.ss58.SS58Type;
import io.emeraldpay.polkaj.types.Address;
import io.emeraldpay.polkaj.types.DotAmount;
import io.emeraldpay.polkaj.types.Hash512;

public class ExtrinsicReader<CALL extends ExtrinsicCall> implements ScaleReader<Extrinsic<CALL>> {

    private final TransactionInfoReader transactionInfoReader;

    private final ScaleReader<CALL> callScaleReader;

    public ExtrinsicReader(ScaleReader<CALL> callScaleReader, SS58Type.Network network) {
        this.callScaleReader = callScaleReader;
        transactionInfoReader = new TransactionInfoReader(network);
    }

    @Override
    public Extrinsic<CALL> read(ScaleCodecReader rdr) {
        byte[] internal = rdr.readByteArray();
        rdr = new ScaleCodecReader(internal);
        int type = rdr.readByte();
        boolean signed = (Extrinsic.TYPE_BIT_SIGNED & type) > 0;
        int version = Extrinsic.TYPE_UNMASK_VERSION & type;
        if (!signed) {
            throw new IllegalStateException("Trying to read unsigned extrinsic");
        }
        if (version != 4) {
            throw new IllegalStateException("Trying to read unsupported version: " + version);
        }
        Extrinsic<CALL> result = new Extrinsic<>();
        result.setTx(rdr.read(transactionInfoReader));
        result.setCall(rdr.read(callScaleReader));
        return result;
    }

    static class TransactionInfoReader implements ScaleReader<Extrinsic.TransactionInfo> {

        private final SS58Type.Network network;

        public TransactionInfoReader(SS58Type.Network network) {
            this.network = network;
        }

        @Override
        public Extrinsic.TransactionInfo read(ScaleCodecReader rdr) {
            Extrinsic.TransactionInfo result = new Extrinsic.TransactionInfo();
            result.setSender(new Address(network, rdr.readUint256()));
            result.setSignatureType(Extrinsic.SignatureType.fromCode(rdr.readByte()));
            result.setSignature(new Hash512(rdr.readByteArray(64)));
            result.setEra(rdr.readCompactInt());
            result.setNonce(rdr.read(ScaleCodecReader.COMPACT_BIGINT).longValueExact());
            result.setTip(new DotAmount(rdr.read(ScaleCodecReader.COMPACT_BIGINT)));
            return result;
        }
    }
}
