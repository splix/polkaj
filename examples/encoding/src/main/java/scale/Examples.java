package scale;

import io.emeraldpay.pjc.scale.ScaleCodecReader;
import io.emeraldpay.pjc.scale.ScaleCodecWriter;
import io.emeraldpay.pjc.scale.UnionValue;
import io.emeraldpay.pjc.scale.reader.*;
import io.emeraldpay.pjc.scale.writer.CompactBigIntWriter;
import io.emeraldpay.pjc.scale.writer.CompactUIntWriter;
import io.emeraldpay.pjc.scale.writer.UInt32Writer;
import io.emeraldpay.pjc.scale.writer.UnionWriter;
import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public class Examples {

    private byte[] readSomeData() {
        return new byte[0];
    }

    public void readManually() {
        byte[] msg = readSomeData();
        ScaleCodecReader rdr = new ScaleCodecReader(msg);

        // there are few shorthand methods for common types

        // to read a single byte, and convert it to int
        int a = rdr.readUByte();
        // to read a number encoded as Compact Int
        int b = rdr.readCompactInt();

        // otherwise you should use a ScaleReader<T> readers

        // UInt32Reader reads longs that are encoded as 32 bit values
        long c = rdr.read(new UInt32Reader());
        // CompactBigIntReader reads a BigInteger encoded as CompactInt, i.e. values up to 2^536-1
        BigInteger d = rdr.read(new CompactBigIntReader());

        // of, if a value is optional:
        Optional<Long> optionalC = rdr.readOptional(new UInt32Reader());

        // to read a list of, say, booleans you should use ListReader<T> with reader for items
        List<Boolean> e = rdr.read(new ListReader<>(new BoolReader()));

        // read an enumerated union, depending on tag in the encoded message, it will use different readers
        UnionValue<Number> f = rdr.read(new UnionReader<>(
                // value with tag 0 is read as unsigned long
                new UInt32Reader(),
                // value with tag 1 is read as unsigned long
                new UInt32Reader(),
                // value with tag 2 is read as compact integer
                new CompactUIntReader()
        ));
        System.out.println("Union read #" + f.getIndex() + " = " + f.getValue());
    }

    public void writeManually() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        // first, open writer as try-with-resources
        try(ScaleCodecWriter wrt = new ScaleCodecWriter(buf)) {
            // same as for reading, there are few shorthand methods for common types

            // write a single byte
            wrt.writeByte(1);

            // write a compact integer
            wrt.writeCompact(2);

            // and same as for reader, use ScaleWriter<T> for writing more complex types

            // write unsigned int as 32 bits
            wrt.write(new UInt32Writer(), 3);
            // write big integer as compact integer
            wrt.write(new CompactBigIntWriter(), new BigInteger("112233445566778899", 16));

            // to write an enumerated union you have to define it's structure first
            UnionWriter<Number> union = new UnionWriter<>(
                    // value with tag 0 is read as unsigned long
                    new UInt32Writer(),
                    // value with tag 1 is read as unsigned long
                    new UInt32Writer(),
                    // value with tag 2 is read as compact integer
                    new CompactUIntWriter()
            );
            // then write pass it, with actual value
            // at this case we write under tag 2, which will write actual value 101 as Compact Integer
            wrt.write(union, new UnionValue<>(2, 101));
        }
        System.out.println("Encoded: " + Hex.encodeHexString(buf.toByteArray()));
    }
}
