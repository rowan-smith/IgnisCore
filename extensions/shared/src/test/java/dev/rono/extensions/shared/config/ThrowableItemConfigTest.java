package dev.rono.extensions.shared.config;

import dev.rono.igniscore.api.model.ItemDefinition;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ThrowableItemConfigTest {
    @Test
    void readsThrowableFieldsFromCustomData() {
        ItemDefinition definition = ItemDefinition.builder("grenade")
                .customData(Map.of(
                        "throw_velocity", 1.2,
                        "fuse_ticks", 40,
                        "power", 4.0,
                        "fire", false))
                .build();

        ThrowableItemConfig config = ExtensionConfigs.throwable(definition);

        assertEquals(1.2, config.throwVelocity());
        assertEquals(40, config.fuseTicks());
        assertEquals(4.0, config.power());
        assertFalse(config.fire());
    }
}
