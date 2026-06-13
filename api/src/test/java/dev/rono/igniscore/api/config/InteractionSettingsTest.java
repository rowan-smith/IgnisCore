package dev.rono.igniscore.api.config;

import dev.rono.igniscore.api.port.IgnisInteraction;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InteractionSettingsTest {
    @Test
    void resolvesConfiguredItemActions() {
        Map<String, Object> settings = Map.of(
                "right_click", Map.of("action", "throw"),
                "left_click", Map.of("action", "assign_bomb")
        );

        assertEquals("throw", InteractionSettings.itemAction(settings, IgnisInteraction.RIGHT_CLICK_AIR));
        assertEquals("throw", InteractionSettings.itemAction(settings, IgnisInteraction.RIGHT_CLICK_BLOCK));
        assertEquals("assign_bomb", InteractionSettings.itemAction(settings, IgnisInteraction.LEFT_CLICK_BLOCK));
        assertEquals("", InteractionSettings.itemAction(settings, IgnisInteraction.PHYSICAL));
        assertTrue(InteractionSettings.handles(settings, IgnisInteraction.RIGHT_CLICK_AIR));
        assertFalse(InteractionSettings.handles(settings, IgnisInteraction.PHYSICAL));
    }
}
