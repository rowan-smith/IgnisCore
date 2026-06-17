package dev.rono.igniscore.api.event;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.PlacedBlock;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;

/**
 * Fuse and active-block events backed by a {@link RuntimeBlockInstance}.
 *
 * <p>{@link #block()} returns the placed block; {@link #instance()} exposes fuse
 * countdown, display entities, and per-instance data.</p>
 */
public interface RuntimeBlockEvent extends BlockEvent {

    RuntimeBlockInstance instance();

    @Override
    default PlacedBlock block() {
        return PlacedBlock.from(instance());
    }
}
