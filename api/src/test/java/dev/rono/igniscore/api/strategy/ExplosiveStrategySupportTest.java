package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExplosiveStrategySupportTest {
    @Test
    void resolvePowerUsesRadiusWhenPresent() {
        BlockDefinition definition = blockDefinition(Map.of("power", 2.0, "multiplier", 3.0), 12.0);

        assertEquals(36.0f, ExplosiveStrategySupport.resolvePower(definition, 4.0));
    }

    @Test
    void resolvePowerFallsBackToCustomDataWhenRadiusIsZero() {
        BlockDefinition definition = blockDefinition(Map.of("power", 5.0, "multiplier", 2.0), 0.0);

        assertEquals(10.0f, ExplosiveStrategySupport.resolvePower(definition, 4.0));
    }

    @Test
    void resolvePowerForItemsUsesCustomDataOnly() {
        ItemDefinition definition = new ItemDefinition(
                "grenade",
                "snowball",
                Component.text("Grenade"),
                List.of(),
                "grenade",
                Map.of("power", 4.0, "multiplier", 1.5),
                Map.of(),
                20001,
                "grenade-item",
                "icon.png"
        );

        assertEquals(6.0f, ExplosiveStrategySupport.resolvePower(definition, 2.0));
    }

    @Test
    void customReadersHandleNumbersAndBooleans() {
        Map<String, Object> customData = Map.of(
                "power", 7,
                "fire", true,
                "blockDamage", false
        );

        assertEquals(7.0, ExplosiveStrategySupport.customDouble(customData, "power", 1.0));
        assertEquals(1.0, ExplosiveStrategySupport.customDouble(customData, "missing", 1.0));
        assertTrue(ExplosiveStrategySupport.customBoolean(customData, "fire", false));
        assertFalse(ExplosiveStrategySupport.customBoolean(customData, "blockDamage", true));
        assertTrue(ExplosiveStrategySupport.customBoolean(customData, "missing", true));
    }

    private BlockDefinition blockDefinition(Map<String, Object> customData, double radius) {
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
                80,
                radius,
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
