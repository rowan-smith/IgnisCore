package dev.rono.igniscore.api.event;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;

/**
 * Fired when a placed custom block is removed from the world.
 */
public final class BlockBreakEvent {
    private final BlockDefinition definition;
    private final IgnisLocation location;
    private final IgnisItem droppedItem;

    public BlockBreakEvent(BlockDefinition definition, IgnisLocation location, IgnisItem droppedItem) {
        this.definition = definition;
        this.location = location;
        this.droppedItem = droppedItem;
    }

    public BlockDefinition definition() {
        return definition;
    }

    public IgnisLocation location() {
        return location;
    }

    public IgnisItem droppedItem() {
        return droppedItem;
    }
}
