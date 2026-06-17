package dev.rono.igniscore.api.config;

import dev.rono.igniscore.api.strategy.StrategyProfile;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockBehaviorConfigTest {
    @Test
    void parsesCombustibleAndSounds() {
        BlockBehaviorConfig behavior = BlockBehaviorConfig.from(ExtensionConfig.of(Map.of(
                "combustible", true,
                "ignition_materials", java.util.List.of("FLINT_AND_STEEL"),
                "sounds", Map.of("place", "BLOCK_BEACON_ACTIVATE", "ignite", "ITEM_FLINTANDSTEEL_USE"))));

        StrategyProfile profile = behavior.merge(StrategyProfile.placed());

        assertTrue(profile.isCombustible());
        assertEquals("BLOCK_BEACON_ACTIVATE", profile.getPlacementSound());
        assertEquals("ITEM_FLINTANDSTEEL_USE", profile.getIgniteSound());
        assertEquals(java.util.List.of("FLINT_AND_STEEL"), profile.getIgnitionMaterials());
    }

    @Test
    void emptyBehaviorIsNoOp() {
        assertTrue(BlockBehaviorConfig.empty().isEmpty());
    }
}
