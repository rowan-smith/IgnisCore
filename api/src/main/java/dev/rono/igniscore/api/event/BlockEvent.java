package dev.rono.igniscore.api.event;

import dev.rono.igniscore.api.config.ExtensionConfig;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;

/**
 * Common shape for block lifecycle events: definition, world position, and custom config.
 */
public interface BlockEvent {

    BlockDefinition definition();

    IgnisLocation block();

    default ExtensionConfig config() {
        return definition().getCustomConfig();
    }
}
