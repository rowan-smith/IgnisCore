package dev.rono.igniscore.item.stickybomb;

import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StrategyTest {
    private static final String EXTENSION_ID = "sticky-bomb";

    @Test
    void manifestMatchesExtensionId() {
        ExtensionManifest manifest = ExtensionTestSupport.loadManifest(StrategyTest.class, "item-extension.yml");
        assertEquals(EXTENSION_ID, manifest.getId());
        assertEquals("dev.rono.igniscore.item.stickybomb.Strategy", manifest.getStrategyClass());
    }
}
