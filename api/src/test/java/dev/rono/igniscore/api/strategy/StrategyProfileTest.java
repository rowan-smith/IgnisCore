package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.CustomBlockAction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StrategyProfileTest {
    @Test
    void defaultsAreNeutral() {
        StrategyProfile profile = StrategyProfile.defaults();

        assertFalse(profile.isCombustible());
        assertTrue(profile.isPlaceable());
        assertTrue(profile.isBreakable());
        assertEquals(0, profile.getDefaultFuse());
        assertEquals(0.0, profile.getDefaultRadius());
        assertEquals(CustomBlockAction.NONE, profile.getLeftClickAction());
        assertEquals(CustomBlockAction.NONE, profile.getRightClickAction());
        assertTrue(profile.getIgnitionMaterials().isEmpty());
        assertEquals(1.0, profile.getDisplayScale());
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
}
