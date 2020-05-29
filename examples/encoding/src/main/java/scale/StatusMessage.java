package scale;

import io.emeraldpay.polkaj.scale.ScaleCodecReader;
import io.emeraldpay.polkaj.scale.ScaleCodecWriter;
import io.emeraldpay.polkaj.scale.ScaleReader;
import io.emeraldpay.polkaj.scale.ScaleWriter;
import io.emeraldpay.polkaj.types.Hash256;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

class StatusMessage {

    public static void main(String[] args) throws IOException {
        // Read Status message encoded with SCALE codec
        byte[] msg = readMessage();
        // Initialize SCALE Reader
        ScaleCodecReader rdr = new ScaleCodecReader(msg);
        // Call it providing a custom reader for the expected class
        Status status = rdr.read(new StatusReader());

        // All read
        System.out.println("Decoded Status: height=" + status.height + ", hash=" + status.bestHash.toString());

        // Write status as bytes
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        ScaleCodecWriter writer = new ScaleCodecWriter(buf);
        writer.write(new StatusWriter(), status);
        // don't forget to close writer
        writer.close();

        System.out.println("Encoded Status: " + Hex.encodeHexString(buf.toByteArray()));
    }

    // Just for the example we use hardcoded value
    static byte[] readMessage() {
        byte[] msg = new byte[0];
        try {
            msg = Hex.decodeHex(
                    "000600000003000000017d010000" +
                            "bb931fd17f85fb26e8209eb7af5747258163df29a7dd8f87fa7617963fcfa1aa" +
                            "b0a8d493285c2df73290dfb7e61f870f17b41801197a149ca93654499ea3dafe0400"
            );
        } catch (DecoderException e) {
            e.printStackTrace();
        }
        return msg;
    }

    // Status class which we expect to read
    static
    class Status {
        private long version;
        private long minVersion;
        private byte roles;
        private long height;
        private Hash256 bestHash;
        private Hash256 genesis;
    }

    // SCALE Reader for Status class.
    // All of the data can be read manually, but ScaleReader interface allows to organize it into individual components
    static
    class StatusReader implements ScaleReader<Status> {

        @Override
        public Status read(ScaleCodecReader rdr) {
            Status status = new Status();
            status.version = rdr.readUint32();
            status.minVersion = rdr.readUint32();
            status.roles = rdr.readByte();
            rdr.skip(1);
            status.height = rdr.readUint32();
            status.bestHash = new Hash256(rdr.readUint256());
            status.genesis = new Hash256(rdr.readUint256());
            return status;
        }
    }

    static
    class StatusWriter implements ScaleWriter<Status> {

        @Override
        public void write(ScaleCodecWriter wrt, Status value) throws IOException {
            wrt.writeUint32(value.version);
            wrt.writeUint32(value.minVersion);
            wrt.writeByte(value.roles);
            wrt.writeByte(0);
            wrt.writeUint32(value.height);
            wrt.writeUint256(value.bestHash.getBytes());
            wrt.writeUint256(value.genesis.getBytes());
        }
    }
}