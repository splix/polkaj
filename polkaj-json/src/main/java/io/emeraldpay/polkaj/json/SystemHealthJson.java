package io.emeraldpay.polkaj.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class SystemHealthJson {

    @JsonProperty("isSyncing")
    private Boolean syncing;
    private Integer peers;
    private Boolean shouldHavePeers;

    public Boolean getSyncing() {
        return syncing;
    }

    public void setSyncing(Boolean syncing) {
        this.syncing = syncing;
    }

    public Integer getPeers() {
        return peers;
    }

    public void setPeers(Integer peers) {
        this.peers = peers;
    }

    public Boolean getShouldHavePeers() {
        return shouldHavePeers;
    }

    public void setShouldHavePeers(Boolean shouldHavePeers) {
        this.shouldHavePeers = shouldHavePeers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SystemHealthJson)) return false;
        SystemHealthJson that = (SystemHealthJson) o;
        return Objects.equals(syncing, that.syncing) &&
                Objects.equals(peers, that.peers) &&
                Objects.equals(shouldHavePeers, that.shouldHavePeers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(syncing, peers, shouldHavePeers);
    }
}
