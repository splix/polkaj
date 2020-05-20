package io.emeraldpay.pjc.json;

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
}
