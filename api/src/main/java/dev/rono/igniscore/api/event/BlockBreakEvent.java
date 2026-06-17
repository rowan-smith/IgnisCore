package dev.rono.igniscore.api.event;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.PlacedBlock;
import dev.rono.igniscore.api.port.IgnisItem;

/**
 * Fired when a placed custom block is removed from the world.
 */
public final class BlockBreakEvent implements BlockEvent {
    private final PlacedBlock block;
    private final IgnisItem droppedItem;

    public BlockBreakEvent(BlockDefinition definition, dev.rono.igniscore.api.port.IgnisLocation location, IgnisItem droppedItem) {
        this(PlacedBlock.of(definition, location), droppedItem);
    }

    public BlockBreakEvent(PlacedBlock block, IgnisItem droppedItem) {
        this.block = block;
        this.droppedItem = droppedItem;
    }

    @Override
    public PlacedBlock block() {
        return block;
    }

    /**
     * @deprecated use {@link #block()}{@code .location()}
     */
    @Deprecated
    public dev.rono.igniscore.api.port.IgnisLocation location() {
        return block.location();
    }

    public IgnisItem droppedItem() {
        return droppedItem;
    }
}
