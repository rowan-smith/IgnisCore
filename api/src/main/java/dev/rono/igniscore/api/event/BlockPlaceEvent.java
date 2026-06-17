package dev.rono.igniscore.api.event;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;

/**
 * Fired after a custom block is registered at a world location.
 */
public final class BlockPlaceEvent implements BlockEvent {
    private final BlockDefinition definition;
    private final IgnisLocation location;
    private final IgnisItem placedFrom;

    public BlockPlaceEvent(BlockDefinition definition, IgnisLocation location, IgnisItem placedFrom) {
        this.definition = definition;
        this.location = location;
        this.placedFrom = placedFrom;
    }

    @Override
    public BlockDefinition definition() {
        return definition;
    }

    @Override
    public IgnisLocation block() {
        return location;
    }

    /**
     * @deprecated use {@link #block()}
     */
    @Deprecated
    public IgnisLocation location() {
        return location;
    }

    public IgnisItem placedFrom() {
        return placedFrom;
    }
}
