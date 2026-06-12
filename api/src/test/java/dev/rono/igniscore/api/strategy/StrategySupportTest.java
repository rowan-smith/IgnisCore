package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.model.BlockDefinition;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StrategySupportTest {
    @Test
    void resolvePowerUsesRadiusAndMultiplierFromCustomData() {
        BlockDefinition definition = blockDefinition(Map.of("radius", 12.0, "multiplier", 3.0), Map.of());

        assertEquals(36.0f, StrategySupport.resolvePower(definition, 4.0));
    }

    @Test
    void resolvePowerFallsBackToPowerWhenRadiusMissing() {
        BlockDefinition definition = blockDefinition(Map.of("power", 10.0), Map.of());

        assertEquals(10.0f, StrategySupport.resolvePower(definition, 4.0));
    }

    @Test
    void resolvePowerUsesDefaultWhenCustomDataEmpty() {
        BlockDefinition definition = blockDefinition(Map.of(), Map.of());

        assertEquals(6.0f, StrategySupport.resolvePower(definition, 6.0));
    }

    @Test
    void readsCustomValuesFromMap() {
        Map<String, Object> customData = Map.of(
                "power", 7.0,
                "fire", true,
                "blockDamage", false
        );

        assertEquals(7.0, StrategySupport.customDouble(customData, "power", 1.0));
        assertEquals(1.0, StrategySupport.customDouble(customData, "missing", 1.0));
        assertTrue(StrategySupport.customBoolean(customData, "fire", false));
        assertFalse(StrategySupport.customBoolean(customData, "blockDamage", true));
        assertTrue(StrategySupport.customBoolean(customData, "missing", true));
    }

    private BlockDefinition blockDefinition(Map<String, Object> customData, Map<String, Object> unused) {
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
                "default",
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
