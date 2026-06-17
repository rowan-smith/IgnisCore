package dev.rono.igniscore.api.config;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockBehaviorConfigTest {
    @Test
    void parsesStandardSurfaceBehavior() {
        BlockBehaviorConfig behavior = BlockBehaviorConfig.from(ExtensionConfig.of(Map.of(
                "combustible", true,
                "left_click_block", "break",
                "right_click_block", "ignite",
                "left_click_air", "none",
                "right_click_air", "none",
                "ignition_materials", java.util.List.of("FLINT_AND_STEEL"),
                "sounds", Map.of("place", "BLOCK_BEACON_ACTIVATE", "ignite", "ITEM_FLINTANDSTEEL_USE"))));

        StrategyProfile profile = behavior.merge(StrategyProfile.placed());

        assertTrue(profile.isCombustible());
        assertEquals(CustomBlockAction.BREAK, profile.getLeftClickAction());
        assertEquals(CustomBlockAction.IGNITE, profile.getRightClickAction());
        assertEquals("BLOCK_BEACON_ACTIVATE", profile.getPlacementSound());
        assertEquals("ITEM_FLINTANDSTEEL_USE", profile.getIgniteSound());
        assertEquals(CustomBlockAction.BREAK,
                behavior.resolve(IgnisInteraction.LEFT_CLICK_BLOCK, profile, "AIR"));
        assertEquals(CustomBlockAction.IGNITE,
                behavior.resolve(IgnisInteraction.RIGHT_CLICK_BLOCK, profile, "FLINT_AND_STEEL"));
        assertEquals(CustomBlockAction.NONE,
                behavior.resolve(IgnisInteraction.RIGHT_CLICK_AIR, profile, "AIR"));
    }

    @Test
    void openRightClickUsesConfiguredActionWhenNotIgniting() {
        BlockBehaviorConfig behavior = BlockBehaviorConfig.from(ExtensionConfig.of(Map.of(
                "combustible", false,
                "left_click_block", "break",
                "right_click_block", "open")));
        StrategyProfile profile = behavior.merge(StrategyProfile.placed());

        assertFalse(profile.isCombustible());
        assertEquals(CustomBlockAction.OPEN,
                behavior.resolve(IgnisInteraction.RIGHT_CLICK_BLOCK, profile, "STICK"));
    }
}
