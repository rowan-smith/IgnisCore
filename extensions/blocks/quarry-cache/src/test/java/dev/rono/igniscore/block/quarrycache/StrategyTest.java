package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.config.BlockBehaviorConfig;
import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.PlacedClickSupport;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StrategyTest {
    @Test
    void manifestMatchesExtensionId() {
        ExtensionManifest manifest = ExtensionTestSupport.loadManifest(StrategyTest.class, "block-extension.yml");
        assertEquals("quarry-cache", manifest.getId());
        assertEquals("dev.rono.igniscore.block.quarrycache.Strategy", manifest.getStrategyClass());
    }

    @Test
    void configDeclaresOpenSurfaceBehavior() {
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(StrategyTest.class, "quarry-cache", 10001);
        Strategy strategy = new Strategy(ExtensionTestSupport.noopContext());

        assertNotNull(strategy);
        assertFalse(BlockBehaviorConfig.from(definition.getBehaviorConfig()).combustible());
        assertEquals(CustomBlockAction.BREAK,
                PlacedClickSupport.resolve(definition, CustomBlockAction.BREAK, CustomBlockAction.OPEN,
                        IgnisInteraction.LEFT_CLICK_BLOCK, "AIR"));
        assertEquals(CustomBlockAction.OPEN,
                PlacedClickSupport.resolve(definition, CustomBlockAction.BREAK, CustomBlockAction.OPEN,
                        IgnisInteraction.RIGHT_CLICK_BLOCK, "AIR"));
    }
}
