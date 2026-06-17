package dev.rono.igniscore.item.fragmentationgrenade;

import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StrategyTest {
    @Test
    void manifestMatchesExtensionId() {
        ExtensionManifest manifest = ExtensionTestSupport.loadManifest(StrategyTest.class, "item-extension.yml");
        assertEquals("fragmentation-grenade", manifest.getId());
    }

    @Test
    void strategyLoadsConfig() {
        ItemDefinition definition = ExtensionTestSupport.loadItemDefinition(StrategyTest.class, "fragmentation-grenade", 10001);
        Strategy strategy = new Strategy(ExtensionTestSupport.noopContext());
        assertNotNull(strategy);
        assertEquals("fragmentation-grenade", definition.getId());
    }
}
