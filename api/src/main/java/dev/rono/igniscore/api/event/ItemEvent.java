package dev.rono.igniscore.api.event;

import dev.rono.igniscore.api.config.ExtensionConfig;
import dev.rono.igniscore.api.model.ItemDefinition;

/**
 * Common shape for item lifecycle events.
 */
public interface ItemEvent {

    ItemDefinition definition();

    default ExtensionConfig config() {
        return definition().getCustomConfig();
    }
}
