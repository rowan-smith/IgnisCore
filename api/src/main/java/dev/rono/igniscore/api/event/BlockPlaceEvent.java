package dev.rono.igniscore.api.event;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.PlacedBlock;
import dev.rono.igniscore.api.port.IgnisItem;

/**
 * Fired after a custom block is registered at a world location.
 */
public final class BlockPlaceEvent implements BlockEvent {
    private final PlacedBlock block;
    private final IgnisItem placedFrom;

    public BlockPlaceEvent(BlockDefinition definition, dev.rono.igniscore.api.port.IgnisLocation location, IgnisItem placedFrom) {
        this(PlacedBlock.of(definition, location), placedFrom);
    }

    public BlockPlaceEvent(PlacedBlock block, IgnisItem placedFrom) {
        this.block = block;
        this.placedFrom = placedFrom;
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

    public IgnisItem placedFrom() {
        return placedFrom;
    }
}
