package dev.rono.extensions.shared.config;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;

/**
 * Convenience entry points for typed extension configuration records.
 */
public final class ExtensionConfigs {
    private ExtensionConfigs() {
    }

    public static ExplosionConfig explosion(BlockDefinition definition) {
        return ExplosionConfig.from(definition);
    }

    public static ThrowableItemConfig throwable(ItemDefinition definition) {
        return ThrowableItemConfig.from(definition);
    }
}
