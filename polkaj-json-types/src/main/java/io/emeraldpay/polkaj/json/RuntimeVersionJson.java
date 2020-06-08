package io.emeraldpay.polkaj.json;

import java.util.List;
import java.util.Objects;

public class RuntimeVersionJson {

    private List<List<?>> apis;
    private Integer authoringVersion;
    private String implName;
    private Integer implVersion;
    private String specName;
    private Integer specVersion;
    private Integer transactionVersion;

    public List<List<?>> getApis() {
        return apis;
    }

    public void setApis(List<List<?>> apis) {
        this.apis = apis;
    }

    public Integer getAuthoringVersion() {
        return authoringVersion;
    }

    public void setAuthoringVersion(Integer authoringVersion) {
        this.authoringVersion = authoringVersion;
    }

    public String getImplName() {
        return implName;
    }

    public void setImplName(String implName) {
        this.implName = implName;
    }

    public Integer getImplVersion() {
        return implVersion;
    }

    public void setImplVersion(Integer implVersion) {
        this.implVersion = implVersion;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public Integer getSpecVersion() {
        return specVersion;
    }

    public void setSpecVersion(Integer specVersion) {
        this.specVersion = specVersion;
    }

    public Integer getTransactionVersion() {
        return transactionVersion;
    }

    public void setTransactionVersion(Integer transactionVersion) {
        this.transactionVersion = transactionVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RuntimeVersionJson)) return false;
        RuntimeVersionJson that = (RuntimeVersionJson) o;
        return Objects.equals(apis, that.apis) &&
                Objects.equals(authoringVersion, that.authoringVersion) &&
                Objects.equals(implName, that.implName) &&
                Objects.equals(implVersion, that.implVersion) &&
                Objects.equals(specName, that.specName) &&
                Objects.equals(specVersion, that.specVersion) &&
                Objects.equals(transactionVersion, that.transactionVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apis, authoringVersion, implName, implVersion, specName, specVersion, transactionVersion);
    }
}
