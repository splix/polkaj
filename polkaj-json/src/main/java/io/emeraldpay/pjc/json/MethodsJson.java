package io.emeraldpay.pjc.json;

import java.util.List;
import java.util.Objects;

/**
 * List of RPC methods.
 * Standard response to <code>rpc_methods</code> command.
 */
public class MethodsJson {

    private List<String> methods;
    private Integer version;

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MethodsJson)) return false;
        MethodsJson that = (MethodsJson) o;
        return Objects.equals(methods, that.methods) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methods, version);
    }
}
