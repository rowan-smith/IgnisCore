package dev.rono.igniscore.api.event;

import dev.rono.igniscore.api.model.PlacedBlock;
import dev.rono.igniscore.api.port.IgnisItem;

/**
 * Fired when a placed custom block is removed from the world.
 */
public final class BlockBreakEvent implements BlockEvent {
    private final PlacedBlock block;
    private final IgnisItem droppedItem;

    public BlockBreakEvent(PlacedBlock block, IgnisItem droppedItem) {
        this.block = block;
        this.droppedItem = droppedItem;
    }

    @Override
    public PlacedBlock block() {
        return block;
    }

    public IgnisItem droppedItem() {
        return droppedItem;
    }
}
