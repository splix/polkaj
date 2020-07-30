package io.emeraldpay.polkaj.scaletypes;

import java.util.Objects;

/**
 * Base definitions for Extrinsic Calls.
 * Contains reference to a runtime function, with module index + function index on that module.
 *
 * @see Metadata
 * @see Metadata.Module
 * @see Metadata.Call
 */
public abstract class ExtrinsicCall {

    /**
     * Module index in metadata
     * @see Metadata.Module
     */
    private int moduleIndex;

    /**
     * Call index in module
     * @see Metadata.Call
     */
    private int callIndex;

    public ExtrinsicCall() {
    }

    public ExtrinsicCall(int moduleIndex, int callIndex) {
        this();
        this.moduleIndex = moduleIndex;
        this.callIndex = callIndex;
    }

    public int getModuleIndex() {
        return moduleIndex;
    }

    public void setModuleIndex(int moduleIndex) {
        this.moduleIndex = moduleIndex;
    }

    public int getCallIndex() {
        return callIndex;
    }

    public void setCallIndex(int callIndex) {
        this.callIndex = callIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExtrinsicCall)) return false;
        if (!((ExtrinsicCall)o).canEquals(this)) return false;
        ExtrinsicCall that = (ExtrinsicCall) o;
        return moduleIndex == that.moduleIndex &&
                callIndex == that.callIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleIndex, callIndex);
    }

    public boolean canEquals(Object o) {
        return (o instanceof ExtrinsicCall);
    }
}
