package dev.rono.igniscore.block.kelpcompressor;

import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StrategyTest {
    @Test
    void manifestMatchesExtensionId() {
        ExtensionManifest manifest = ExtensionTestSupport.loadManifest(StrategyTest.class, "block-extension.yml");
        assertEquals("kelp-compressor", manifest.getId());
    }

    @Test
    void strategyExposesProfile() {
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(StrategyTest.class, "kelp-compressor", 10001);
        Strategy strategy = new Strategy(ExtensionTestSupport.noopContext());
        assertNotNull(strategy.profile(definition));
    }
}
