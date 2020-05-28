package io.emeraldpay.pjc.json;

import io.emeraldpay.pjc.types.Hash256;

import java.util.Objects;

public class PeerJson {

    private Hash256 bestHash;
    private Long bestNumber;
    private String peerId;
    private Integer protocolVersion;
    private String roles;

    public Hash256 getBestHash() {
        return bestHash;
    }

    public void setBestHash(Hash256 bestHash) {
        this.bestHash = bestHash;
    }

    public Long getBestNumber() {
        return bestNumber;
    }

    public void setBestNumber(Long bestNumber) {
        this.bestNumber = bestNumber;
    }

    public String getPeerId() {
        return peerId;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    public Integer getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(Integer protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PeerJson)) return false;
        PeerJson peerJson = (PeerJson) o;
        return Objects.equals(bestHash, peerJson.bestHash) &&
                Objects.equals(bestNumber, peerJson.bestNumber) &&
                Objects.equals(peerId, peerJson.peerId) &&
                Objects.equals(protocolVersion, peerJson.protocolVersion) &&
                Objects.equals(roles, peerJson.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bestHash, bestNumber, peerId, protocolVersion, roles);
    }
}
