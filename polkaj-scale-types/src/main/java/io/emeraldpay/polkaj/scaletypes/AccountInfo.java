package io.emeraldpay.polkaj.scaletypes;

import java.util.Objects;

public class AccountInfo {

    private Long nonce;
    private Integer refcount;
    private AccountData data;

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    public Integer getRefcount() {
        return refcount;
    }

    public void setRefcount(Integer refcount) {
        this.refcount = refcount;
    }

    public AccountData getData() {
        return data;
    }

    public void setData(AccountData data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountInfo)) return false;
        AccountInfo that = (AccountInfo) o;
        return Objects.equals(nonce, that.nonce) &&
                Objects.equals(refcount, that.refcount) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nonce, refcount, data);
    }
}
