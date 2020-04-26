package io.emeraldpay.pjc.scale;

import io.emeraldpay.pjc.scale.writer.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public class ScaleCodecWriter extends OutputStream {

    public static final CompactUIntWriter COMPACT_UINT = new CompactUIntWriter();
    public static final UInt16Writer UINT16 = new UInt16Writer();
    public static final UInt32Writer UINT32 = new UInt32Writer();
    public static final ULong32Writer ULONG32 = new ULong32Writer();
    public static final BoolWriter BOOL = new BoolWriter();
    public static final BoolOptionalWriter BOOL_OPT = new BoolOptionalWriter();

    private OutputStream out;

    public ScaleCodecWriter(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    public void writeUint256(byte[] value) throws IOException {
        if (value.length != 32) {
            throw new IllegalArgumentException("Value must be 32 byte array");
        }
        this.write(value, 0, value.length);
    }

    public void writeByteArray(byte[] value) throws IOException {
        writeCompact(value.length);
        this.write(value, 0, value.length);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    public <T> void write(ScaleWriter<T> writer, T value) throws IOException {
        writer.write(this, value);
    }

    public void writeUint16(int value) throws IOException {
        UINT16.write(this, value);
    }

    public void writeUint32(int value) throws IOException {
        UINT32.write(this, value);
    }

    public void writeUint32(long value) throws IOException {
        ULONG32.write(this, value);
    }

    public void writeCompact(int value) throws IOException {
        COMPACT_UINT.write(this, value);
    }

    @SuppressWarnings("unchecked")
    public <T> void writeOptional(ScaleWriter<T> writer, T value) throws IOException {
        if (writer instanceof BoolOptionalWriter || writer instanceof BoolWriter) {
            BOOL_OPT.write(this, (Optional<Boolean>) Optional.ofNullable(value));
        } else {
            if (value == null) {
                BOOL.write(this, false);
            } else {
                BOOL.write(this, true);
                writer.write(this, value);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void writeOptional(ScaleWriter<T> writer, Optional<T> value) throws IOException {
        if (writer instanceof BoolOptionalWriter || writer instanceof BoolWriter) {
            BOOL_OPT.write(this, (Optional<Boolean>) value);
        } else {
            if (value.isEmpty()) {
                BOOL.write(this, false);
            } else {
                BOOL.write(this, true);
                writer.write(this, value.get());
            }
        }
    }
}
