package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.CustomBlockAction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StrategyProfileTest {
    @Test
    void defaultsMatchExplosiveBaseline() {
        StrategyProfile profile = StrategyProfile.defaults();

        assertTrue(profile.isCombustible());
        assertTrue(profile.isPlaceable());
        assertTrue(profile.isBreakable());
        assertEquals(80, profile.getDefaultFuse());
        assertEquals(4.0, profile.getDefaultRadius());
        assertEquals(CustomBlockAction.BREAK, profile.getLeftClickAction());
        assertEquals(CustomBlockAction.IGNITE, profile.getRightClickAction());
        assertEquals(List.of("FLINT_AND_STEEL", "FIRE_CHARGE", "FLINT"), profile.getIgnitionMaterials());
        assertEquals("ITEM_FLINTANDSTEEL_USE", profile.getIgniteSound());
        assertEquals(1.01, profile.getDisplayScale());
    }

    @Test
    void toBuilderCreatesMutableCopy() {
        StrategyProfile original = StrategyProfile.builder()
                .combustible(false)
                .defaultFuse(120)
                .defaultRadius(8.0)
                .leftClickAction(CustomBlockAction.NONE)
                .rightClickAction(CustomBlockAction.BREAK)
                .ignitionMaterials(List.of("STICK"))
                .displayScale(2.0)
                .build();

        StrategyProfile rebuilt = original.toBuilder()
                .combustible(true)
                .defaultRadius(10.0)
                .build();

        assertFalse(original.isCombustible());
        assertEquals(120, original.getDefaultFuse());
        assertTrue(rebuilt.isCombustible());
        assertEquals(10.0, rebuilt.getDefaultRadius());
        assertEquals(CustomBlockAction.NONE, rebuilt.getLeftClickAction());
        assertEquals(List.of("STICK"), rebuilt.getIgnitionMaterials());
    }

    @Test
    void explosiveProfileMatchesDedicatedFactory() {
        StrategyProfile profile = StrategyProfiles.explosiveProfile();

        assertTrue(profile.isCombustible());
        assertEquals(CustomBlockAction.BREAK, profile.getLeftClickAction());
        assertEquals(CustomBlockAction.IGNITE, profile.getRightClickAction());
        assertEquals("BLOCK_BEACON_ACTIVATE", profile.getPlacementSound());
        assertEquals("ITEM_FLINTANDSTEEL_USE", profile.getIgniteSound());
    }
}
