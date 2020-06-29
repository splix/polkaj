package io.emeraldpay.polkaj.json;

import io.emeraldpay.polkaj.types.Address;
import io.emeraldpay.polkaj.types.ByteData;

import java.util.Objects;

public class ContractCallRequestJson {

    private Address origin;
    private Address dest;
    private long value;
    private long gasLimit;
    private ByteData inputData = ByteData.empty();

    public Address getOrigin() {
        return origin;
    }

    public void setOrigin(Address origin) {
        this.origin = origin;
    }

    public Address getDest() {
        return dest;
    }

    public void setDest(Address dest) {
        this.dest = dest;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public long getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(long gasLimit) {
        this.gasLimit = gasLimit;
    }

    public ByteData getInputData() {
        return inputData;
    }

    public void setInputData(ByteData inputData) {
        if (inputData == null) {
            throw new NullPointerException("inputData cannot be null. Set empty data istead of null");
        }
        this.inputData = inputData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContractCallRequestJson)) return false;
        ContractCallRequestJson that = (ContractCallRequestJson) o;
        return value == that.value &&
                gasLimit == that.gasLimit &&
                Objects.equals(origin, that.origin) &&
                Objects.equals(dest, that.dest) &&
                inputData.equals(that.inputData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, dest, value, gasLimit, inputData);
    }
}
