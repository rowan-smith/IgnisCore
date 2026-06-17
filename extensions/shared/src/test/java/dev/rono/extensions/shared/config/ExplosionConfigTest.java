package dev.rono.extensions.shared.config;

import dev.rono.igniscore.api.model.BlockDefinition;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExplosionConfigTest {
    @Test
    void readsExplosionFieldsFromCustomData() {
        BlockDefinition definition = BlockDefinition.builder("nuke")
                .customData(Map.of(
                        "fuse", 160,
                        "radius", 30.0,
                        "power", 30.0,
                        "multiplier", 5.0,
                        "fire", true,
                        "blockDamage", true,
                        "screenShake", true))
                .build();

        ExplosionConfig config = ExtensionConfigs.explosion(definition);

        assertEquals(160, config.fuse());
        assertEquals(30.0, config.radius());
        assertEquals(30.0, config.power());
        assertEquals(5.0, config.multiplier());
        assertTrue(config.fire());
        assertTrue(config.blockDamage());
        assertTrue(config.screenShake());
        assertEquals(150.0f, config.resolvedPower(), 0.01f);
    }
}
