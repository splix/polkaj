package io.emeraldpay.polkaj.json;

import io.emeraldpay.polkaj.types.ByteData;
import io.emeraldpay.polkaj.types.Hash256;

import java.util.List;
import java.util.Objects;

public class StorageChangeSetJson {

    private Hash256 block;
    private List<KeyValueOption> changes;

    public Hash256 getBlock() {
        return block;
    }

    public void setBlock(Hash256 block) {
        this.block = block;
    }

    public List<KeyValueOption> getChanges() {
        return changes;
    }

    public void setChanges(List<KeyValueOption> changes) {
        this.changes = changes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StorageChangeSetJson)) return false;
        StorageChangeSetJson that = (StorageChangeSetJson) o;
        return Objects.equals(block, that.block) &&
                Objects.equals(changes, that.changes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(block, changes);
    }

    public static class KeyValueOption {
        private Hash256 key;
        private ByteData data;

        public Hash256 getKey() {
            return key;
        }

        public void setKey(Hash256 key) {
            this.key = key;
        }

        public ByteData getData() {
            return data;
        }

        public void setData(ByteData data) {
            this.data = data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof KeyValueOption)) return false;
            KeyValueOption that = (KeyValueOption) o;
            return Objects.equals(key, that.key) &&
                    Objects.equals(data, that.data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, data);
        }
    }

}
