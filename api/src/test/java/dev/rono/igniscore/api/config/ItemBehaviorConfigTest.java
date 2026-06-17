package dev.rono.igniscore.api.config;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemBehaviorConfigTest {
    @Test
    void mapsConfiguredInteractionsToActionTokens() {
        ItemBehaviorConfig config = ItemBehaviorConfig.from(ExtensionConfig.of(Map.of(
                "left_click_block", "assign",
                "right_click_air", "detonate",
                "right_click_block", "detonate",
                "left_click_air", "none")));

        assertEquals("assign", config.actionFor(dev.rono.igniscore.api.port.IgnisInteraction.LEFT_CLICK_BLOCK).orElseThrow());
        assertEquals("detonate", config.actionFor(dev.rono.igniscore.api.port.IgnisInteraction.RIGHT_CLICK_AIR).orElseThrow());
        assertFalse(config.actionFor(dev.rono.igniscore.api.port.IgnisInteraction.LEFT_CLICK_AIR).isPresent());
        assertTrue(config.triggers(dev.rono.igniscore.api.port.IgnisInteraction.RIGHT_CLICK_BLOCK));
    }
}
