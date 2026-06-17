package dev.rono.igniscore.block.barnbell;

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
        assertEquals("barn-bell", manifest.getId());
    }

    @Test
    void strategyExposesProfile() {
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(StrategyTest.class, "barn-bell", 10001);
        Strategy strategy = new Strategy(ExtensionTestSupport.noopContext());
        assertNotNull(strategy.profile(definition));
    }
}
