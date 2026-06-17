package dev.rono.igniscore.block.shrapnelmine;

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
        assertEquals("shrapnel-mine", manifest.getId());
        assertEquals("dev.rono.igniscore.block.shrapnelmine.Strategy", manifest.getStrategyClass());
    }

    @Test
    void strategyExposesProfileForConfig() {
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(StrategyTest.class, "shrapnel-mine", 10001);
        Strategy strategy = new Strategy(ExtensionTestSupport.noopContext());
        StrategyProfile profile = strategy.profile(definition);
        assertNotNull(profile);
        assertEquals("shrapnel-mine", definition.getId());
    }
}
