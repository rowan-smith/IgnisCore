package dev.rono.igniscore.api.event;

import dev.rono.igniscore.api.model.PlacedBlock;
import dev.rono.igniscore.api.port.IgnisItem;

/**
 * Fired after a custom block is registered at a world location.
 */
public final class BlockPlaceEvent implements BlockEvent {
    private final PlacedBlock block;
    private final IgnisItem placedFrom;

    public BlockPlaceEvent(PlacedBlock block, IgnisItem placedFrom) {
        this.block = block;
        this.placedFrom = placedFrom;
    }

    @Override
    public PlacedBlock block() {
        return block;
    }

    public IgnisItem placedFrom() {
        return placedFrom;
    }
}
