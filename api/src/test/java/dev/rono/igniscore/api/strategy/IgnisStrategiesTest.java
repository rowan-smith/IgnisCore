package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.config.ExtensionConfig;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisInteraction;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IgnisStrategiesTest {
    @Test
    void blocksFacadeExposesProfiles() {
        assertFalse(IgnisStrategies.blocks().placed().hasFuseLifecycle());
        assertTrue(IgnisStrategies.blocks().fuse(40).hasFuseLifecycle());
    }

    @Test
    void itemsFacadeResolvesBehaviorTokens() {
        ItemDefinition definition = new ItemDefinition(
                "test",
                "snowball",
                Component.text("Test"),
                List.of(),
                Map.of(),
                Map.of("right_click_air", "throw"),
                Map.of(),
                10001,
                "test",
                "icon.png");

        assertTrue(IgnisStrategies.items().triggers(definition, IgnisInteraction.RIGHT_CLICK_AIR));
        assertEquals("throw", IgnisStrategies.items().actionFor(definition, IgnisInteraction.RIGHT_CLICK_AIR).orElseThrow());
    }

    @Test
    void dataFacadeReadsItemCustomData() {
        ItemDefinition definition = new ItemDefinition(
                "test",
                "snowball",
                Component.text("Test"),
                List.of(),
                Map.of("power", 6.0),
                Map.of(),
                Map.of(),
                10001,
                "test",
                "icon.png");

        assertEquals(6.0, IgnisStrategies.data().customDouble(definition, "power", 1.0));
    }
}
