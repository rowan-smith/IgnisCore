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
        StrategyProfile profile = StrategyProfile.placed();

        assertFalse(profile.isCombustible());
        assertTrue(profile.isPlaceable());
        assertTrue(profile.isBreakable());
        assertFalse(profile.hasFuseLifecycle());
        assertFalse(profile.hasExplosionRadius());
        assertEquals(0, profile.getDefaultFuse());
        assertEquals(0.0, profile.getDefaultRadius());
        assertEquals(CustomBlockAction.NONE, profile.getLeftClickAction());
        assertEquals(CustomBlockAction.NONE, profile.getRightClickAction());
        assertTrue(profile.getIgnitionMaterials().isEmpty());
        assertEquals(1.0, profile.getDisplayScale());
    }

    @Test
    void fuseFactoryDeclaresFuseLifecycle() {
        StrategyProfile profile = StrategyProfile.fuse(60);

        assertTrue(profile.hasFuseLifecycle());
        assertEquals(60, profile.getDefaultFuse());
        assertFalse(profile.isCombustible());
    }

    @Test
    void combustibleFactorySetsExplosiveDefaults() {
        StrategyProfile profile = StrategyProfile.combustible(80, 4.0);

        assertTrue(profile.isCombustible());
        assertTrue(profile.hasFuseLifecycle());
        assertTrue(profile.hasExplosionRadius());
        assertEquals(80, profile.getDefaultFuse());
        assertEquals(4.0, profile.getDefaultRadius());
        assertEquals(CustomBlockAction.BREAK, profile.getLeftClickAction());
        assertEquals(CustomBlockAction.IGNITE, profile.getRightClickAction());
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
        assertTrue(rebuilt.hasFuseLifecycle());
        assertEquals(10.0, rebuilt.getDefaultRadius());
        assertEquals(CustomBlockAction.NONE, rebuilt.getLeftClickAction());
        assertEquals(List.of("STICK"), rebuilt.getIgnitionMaterials());
    }
}
