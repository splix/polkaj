package io.emeraldpay.polkaj.json;

import io.emeraldpay.polkaj.types.ByteData;
import io.emeraldpay.polkaj.types.Hash256;

import java.util.List;
import java.util.Objects;

public class ReadProofJson {
    private Hash256 at;
    private List<ByteData> proof;

    public Hash256 getAt() {
        return at;
    }

    public void setAt(Hash256 at) {
        this.at = at;
    }

    public List<ByteData> getProof() {
        return proof;
    }

    public void setProof(List<ByteData> proof) {
        this.proof = proof;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReadProofJson)) return false;
        ReadProofJson that = (ReadProofJson) o;
        return Objects.equals(at, that.at) &&
                Objects.equals(proof, that.proof);
    }

    @Override
    public int hashCode() {
        return Objects.hash(at, proof);
    }
}
