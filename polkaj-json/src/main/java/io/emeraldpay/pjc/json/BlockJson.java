package io.emeraldpay.pjc.json;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.emeraldpay.pjc.json.jackson.HexLongDeserializer;
import io.emeraldpay.pjc.json.jackson.HexLongSerializer;
import io.emeraldpay.pjc.types.ByteData;
import io.emeraldpay.pjc.types.Hash256;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BlockJson {

    private List<ByteData> extrinsics = Collections.emptyList();
    private Header header;

    public List<ByteData> getExtrinsics() {
        return extrinsics;
    }

    public void setExtrinsics(List<ByteData> extrinsics) {
        this.extrinsics = extrinsics;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockJson)) return false;
        BlockJson blockJson = (BlockJson) o;
        return Objects.equals(extrinsics, blockJson.extrinsics) &&
                Objects.equals(header, blockJson.header);
    }

    @Override
    public int hashCode() {
        return Objects.hash(extrinsics, header);
    }

    public static class Header {
        private Digest digest;
        private Hash256 extrinsicsRoot;
        @JsonDeserialize(using = HexLongDeserializer.class)
        @JsonSerialize(using = HexLongSerializer.class)
        private Long number;
        private Hash256 parentHash;
        private Hash256 stateRoot;

        public Digest getDigest() {
            return digest;
        }

        public void setDigest(Digest digest) {
            this.digest = digest;
        }

        public Hash256 getExtrinsicsRoot() {
            return extrinsicsRoot;
        }

        public void setExtrinsicsRoot(Hash256 extrinsicsRoot) {
            this.extrinsicsRoot = extrinsicsRoot;
        }

        public Long getNumber() {
            return number;
        }

        public void setNumber(Long number) {
            this.number = number;
        }

        public Hash256 getParentHash() {
            return parentHash;
        }

        public void setParentHash(Hash256 parentHash) {
            this.parentHash = parentHash;
        }

        public Hash256 getStateRoot() {
            return stateRoot;
        }

        public void setStateRoot(Hash256 stateRoot) {
            this.stateRoot = stateRoot;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Header)) return false;
            Header header = (Header) o;
            return Objects.equals(digest, header.digest) &&
                    Objects.equals(extrinsicsRoot, header.extrinsicsRoot) &&
                    Objects.equals(number, header.number) &&
                    Objects.equals(parentHash, header.parentHash) &&
                    Objects.equals(stateRoot, header.stateRoot);
        }

        @Override
        public int hashCode() {
            return Objects.hash(digest, extrinsicsRoot, number, parentHash, stateRoot);
        }

        public static class Digest {
            private List<ByteData> logs = Collections.emptyList();

            public List<ByteData> getLogs() {
                return logs;
            }

            public void setLogs(List<ByteData> logs) {
                this.logs = logs;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof Digest)) return false;
                Digest digest = (Digest) o;
                return Objects.equals(logs, digest.logs);
            }

            @Override
            public int hashCode() {
                return Objects.hash(logs);
            }
        }
    }
}
