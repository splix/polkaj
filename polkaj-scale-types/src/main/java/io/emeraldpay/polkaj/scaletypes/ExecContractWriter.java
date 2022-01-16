package io.emeraldpay.polkaj.scaletypes;

import io.emeraldpay.polkaj.scale.ScaleCodecWriter;
import io.emeraldpay.polkaj.scale.ScaleWriter;
import io.emeraldpay.polkaj.scale.writer.UInt64Writer;
import java.io.IOException;
import java.math.BigInteger;

public class ExecContractWriter<T> implements ScaleWriter<ExecContract<T>> {

  private static final MultiAddressWriter DESTINATION_WRITER = new MultiAddressWriter();
  private static final UInt64Writer U_INT_64_WRITER = new UInt64Writer();
  private final ScaleWriter<T> dataWriter;

  public ExecContractWriter(ScaleWriter<T> dataWriter) {
    this.dataWriter = dataWriter;
  }

  @Override
  public void write(ScaleCodecWriter wrt, ExecContract<T> value) throws IOException {
    wrt.writeByte(value.getModuleIndex());
    wrt.writeByte(value.getCallIndex());
    wrt.write(DESTINATION_WRITER, value.getDestination());
    wrt.write(ScaleCodecWriter.COMPACT_BIGINT, value.getValue().getValue());
    wrt.write(U_INT_64_WRITER, BigInteger.valueOf(value.getGasLimit()));
    wrt.write(dataWriter, value.getData());
  }

}
