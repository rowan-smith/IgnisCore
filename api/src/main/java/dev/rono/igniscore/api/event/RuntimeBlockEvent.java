package dev.rono.igniscore.api.event;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;

/**
 * Fuse and active-block events backed by a {@link RuntimeBlockInstance}.
 */
public interface RuntimeBlockEvent extends BlockEvent {

    RuntimeBlockInstance instance();

    @Override
    default BlockDefinition definition() {
        return instance().getDefinition();
    }

    @Override
    default IgnisLocation block() {
        return instance().getLocation();
    }
}
