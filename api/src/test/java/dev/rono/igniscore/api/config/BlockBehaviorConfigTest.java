package dev.rono.igniscore.api.config;

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

        assertTrue(behavior.combustible());
        assertEquals("BLOCK_BEACON_ACTIVATE", behavior.placementSound());
        assertEquals("ITEM_FLINTANDSTEEL_USE", behavior.igniteSoundOr("fallback"));
        assertEquals(java.util.List.of("FLINT_AND_STEEL"), behavior.ignitionMaterials());
    }

    @Test
    void emptyBehaviorIsNoOp() {
        assertTrue(BlockBehaviorConfig.empty().isEmpty());
    }
}
