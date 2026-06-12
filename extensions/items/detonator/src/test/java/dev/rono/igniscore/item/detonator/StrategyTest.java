package dev.rono.igniscore.item.detonator;

import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StrategyTest {
    private static final String EXTENSION_ID = "detonator";

    @Test
    void manifestMatchesExtensionId() {
        ExtensionManifest manifest = ExtensionTestSupport.loadManifest(StrategyTest.class, "item-extension.yml");
        assertEquals(EXTENSION_ID, manifest.getId());
        assertEquals("dev.rono.igniscore.item.detonator.Strategy", manifest.getStrategyClass());
    }

    @Test
    void strategyInstantiatesForConfig() {
        ItemDefinition definition = ExtensionTestSupport.loadItemDefinition(StrategyTest.class, EXTENSION_ID, 20001);
        Strategy strategy = new Strategy(ExtensionTestSupport.noopContext());
        assertNotNull(strategy);
        assertEquals(EXTENSION_ID, definition.getId());
    }
}
