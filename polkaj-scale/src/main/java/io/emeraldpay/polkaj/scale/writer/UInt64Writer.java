package io.emeraldpay.polkaj.scale.writer;

import io.emeraldpay.polkaj.scale.ScaleCodecWriter;
import io.emeraldpay.polkaj.scale.ScaleWriter;
import java.io.IOException;
import java.math.BigInteger;

public class UInt64Writer implements ScaleWriter<BigInteger> {

  public static final BigInteger MAX_UINT64 = new BigInteger("18446744073709551615");

  @Override
  public void write(ScaleCodecWriter wrt, BigInteger value) throws IOException {
    if (value.compareTo(BigInteger.ZERO) < 0) {
      throw new IllegalArgumentException("Negative values are not supported: " + value);
    }
    if (value.compareTo(MAX_UINT64) > 0) {
      throw new IllegalArgumentException("Value is to big for 64 bits. " + value);
    }
    wrt.directWrite(value.and(BigInteger.valueOf(255)).intValue());
    wrt.directWrite(value.shiftRight(8).and(BigInteger.valueOf(255)).intValue());
    wrt.directWrite(value.shiftRight(16).and(BigInteger.valueOf(255)).intValue());
    wrt.directWrite(value.shiftRight(24).and(BigInteger.valueOf(255)).intValue());
    wrt.directWrite(value.shiftRight(32).and(BigInteger.valueOf(255)).intValue());
    wrt.directWrite(value.shiftRight(40).and(BigInteger.valueOf(255)).intValue());
    wrt.directWrite(value.shiftRight(48).and(BigInteger.valueOf(255)).intValue());
    wrt.directWrite(value.shiftRight(56).and(BigInteger.valueOf(255)).intValue());
  }
}
