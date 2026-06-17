package dev.rono.extensions.shared.strategy;

import dev.rono.igniscore.api.model.BlockDefinition;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExplosionSupportTest {
    @Test
    void resolvePowerUsesRadiusAndMultiplierFromCustomData() {
        BlockDefinition definition = blockDefinition(Map.of("radius", 12.0, "multiplier", 3.0));

        assertEquals(36.0f, ExplosionSupport.resolvePower(definition, 4.0));
    }

    @Test
    void resolvePowerFallsBackToPowerWhenRadiusMissing() {
        BlockDefinition definition = blockDefinition(Map.of("power", 10.0));

        assertEquals(10.0f, ExplosionSupport.resolvePower(definition, 4.0));
    }

    @Test
    void resolvePowerUsesDefaultWhenCustomDataEmpty() {
        BlockDefinition definition = blockDefinition(Map.of());

        assertEquals(6.0f, ExplosionSupport.resolvePower(definition, 6.0));
    }

    @Test
    void fuseReadsCustomData() {
        BlockDefinition definition = blockDefinition(Map.of("fuse", 55));

        assertEquals(55, ExplosionSupport.fuse(definition, 80));
        assertEquals(80, ExplosionSupport.fuse(blockDefinition(Map.of()), 80));
    }

    private BlockDefinition blockDefinition(Map<String, Object> customData) {
        return new BlockDefinition(
                "test",
                "paper",
                "carrot_on_a_stick",
                Component.text("Test"),
                List.of(),
                true,
                true,
                "top.png",
                "side.png",
                "bottom.png",
                customData,
                Map.of(),
                Map.of(),
                Map.of(),
                10001,
                false,
                false,
                false
        );
    }
}
