package dev.rono.igniscore.api.event;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;

/**
 * Fired after a custom block is registered at a world location.
 */
public final class BlockPlaceEvent {
    private final BlockDefinition definition;
    private final IgnisLocation location;
    private final IgnisItem placedFrom;

    public BlockPlaceEvent(BlockDefinition definition, IgnisLocation location, IgnisItem placedFrom) {
        this.definition = definition;
        this.location = location;
        this.placedFrom = placedFrom;
    }

    public BlockDefinition definition() {
        return definition;
    }

    public IgnisLocation location() {
        return location;
    }

    public IgnisItem placedFrom() {
        return placedFrom;
    }
}
