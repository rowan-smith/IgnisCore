package dev.rono.igniscore.block.tripwirecharge;

import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StrategyTest {
    private static final String EXTENSION_ID = "tripwire-charge";

    @Test
    void manifestMatchesExtensionId() {
        ExtensionManifest manifest = ExtensionTestSupport.loadManifest(StrategyTest.class, "block-extension.yml");
        assertEquals(EXTENSION_ID, manifest.getId());
        assertEquals("dev.rono.igniscore.block.tripwirecharge.Strategy", manifest.getStrategyClass());
    }
    @Test
    void strategyConstructs() {
        Strategy strategy = new Strategy(ExtensionTestSupport.noopContext());
        assertNotNull(strategy);
    }
}
