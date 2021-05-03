package io.emeraldpay.polkaj.json;

import java.util.Arrays;
import java.util.Objects;

public class BlockResponseJson {

    private BlockJson block;
    private Object[] justifications;

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

    public Object[] getJustifications() {
        return justifications;
    }

    public void setJustifications(Object[] justifications) {
        this.justifications = justifications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockResponseJson)) return false;
        BlockResponseJson that = (BlockResponseJson) o;
        return Objects.equals(block, that.block) &&
                Arrays.equals(justifications, that.justifications);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(block);
        result = 31 * result + Arrays.hashCode(justifications);
        return result;
    }
}
