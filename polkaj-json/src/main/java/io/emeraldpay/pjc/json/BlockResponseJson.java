package io.emeraldpay.pjc.json;

import java.util.Objects;

public class BlockResponseJson {

    private BlockJson block;
    private Object justification;

    public BlockResponseJson() {
    }

    public BlockResponseJson(BlockJson block) {
        this.block = block;
    }

    public BlockJson getBlock() {
        return block;
    }

    public void setBlock(BlockJson block) {
        this.block = block;
    }

    public Object getJustification() {
        return justification;
    }

    public void setJustification(Object justification) {
        this.justification = justification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockResponseJson)) return false;
        BlockResponseJson that = (BlockResponseJson) o;
        return Objects.equals(block, that.block) &&
                Objects.equals(justification, that.justification);
    }

    @Override
    public int hashCode() {
        return Objects.hash(block, justification);
    }
}
