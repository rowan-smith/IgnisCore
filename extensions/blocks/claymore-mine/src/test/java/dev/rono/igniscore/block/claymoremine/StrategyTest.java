package dev.rono.igniscore.block.claymoremine;

import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StrategyTest {
    private static final String EXTENSION_ID = "claymore-mine";

    @Test
    void manifestMatchesExtensionId() {
        ExtensionManifest manifest = ExtensionTestSupport.loadManifest(StrategyTest.class, "block-extension.yml");
        assertEquals(EXTENSION_ID, manifest.getId());
        assertEquals("dev.rono.igniscore.block.claymoremine.Strategy", manifest.getStrategyClass());
    }

    @Test
    void strategyExposesProfileForConfig() {
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(StrategyTest.class, EXTENSION_ID, 10001);
        Strategy strategy = new Strategy(ExtensionTestSupport.noopContext());
        StrategyProfile profile = strategy.profile(definition);
        assertNotNull(profile);
        assertEquals(EXTENSION_ID, definition.getId());
    }
}
