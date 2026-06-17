package dev.rono.igniscore.item.phantomgrenade;

import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StrategyTest {
    private static final String EXTENSION_ID = "phantom-grenade";

    @Test
    void manifestMatchesExtensionId() {
        ExtensionManifest manifest = ExtensionTestSupport.loadManifest(StrategyTest.class, "item-extension.yml");
        assertEquals(EXTENSION_ID, manifest.getId());
        assertEquals("dev.rono.igniscore.item.phantomgrenade.Strategy", manifest.getStrategyClass());
    }
}
