package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.StrategyProfile;
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
    void strategyExposesOpenProfile() {
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(StrategyTest.class, "quarry-cache", 10001);
        Strategy strategy = new Strategy(ExtensionTestSupport.noopContext());
        StrategyProfile profile = strategy.profile(definition);

        assertNotNull(profile);
        assertFalse(profile.isCombustible());
        assertEquals(CustomBlockAction.BREAK, profile.getLeftClickAction());
        assertEquals(CustomBlockAction.OPEN, profile.getRightClickAction());
    }
}
