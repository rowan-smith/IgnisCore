package ${package};

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
        assertEquals("${extensionId}", manifest.getId());
        assertEquals("${package}.Strategy", manifest.getStrategyClass());
    }

    @Test
    void strategyInstantiatesForConfig() {
        ItemDefinition definition = ExtensionTestSupport.loadItemDefinition(StrategyTest.class, "${extensionId}", 20001);
        Strategy strategy = new Strategy(ExtensionTestSupport.noopContext());
        assertNotNull(strategy);
        assertEquals("${extensionId}", definition.getId());
    }
}
