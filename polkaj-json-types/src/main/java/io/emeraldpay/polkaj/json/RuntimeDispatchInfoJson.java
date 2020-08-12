package io.emeraldpay.polkaj.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.emeraldpay.polkaj.types.DotAmount;

import java.util.Objects;

public class RuntimeDispatchInfoJson {

    @JsonProperty("class")
    private DispatchClass dispatchClass;
    private DotAmount partialFee;
    private Long weight;

    public DispatchClass getDispatchClass() {
        return dispatchClass;
    }

    public void setDispatchClass(DispatchClass dispatchClass) {
        this.dispatchClass = dispatchClass;
    }

    public DotAmount getPartialFee() {
        return partialFee;
    }

    public void setPartialFee(DotAmount partialFee) {
        this.partialFee = partialFee;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RuntimeDispatchInfoJson)) return false;
        RuntimeDispatchInfoJson that = (RuntimeDispatchInfoJson) o;
        return dispatchClass == that.dispatchClass &&
                Objects.equals(partialFee, that.partialFee) &&
                Objects.equals(weight, that.weight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dispatchClass, partialFee, weight);
    }

    public static enum DispatchClass {
        @JsonProperty("normal")
        NORMAL,
        @JsonProperty("operational")
        OPERATIONAL,
        @JsonProperty("mandatory")
        MANDATORY
    }
}
