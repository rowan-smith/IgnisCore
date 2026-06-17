package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.testsupport.NoopExtensionSupport;
import dev.rono.igniscore.testsupport.NoopEventBus;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractIgnisItemStrategyTest {
    @Test
    void readsCustomDataHelpers() {
        TestStrategy strategy = new TestStrategy();
        ItemDefinition definition = new ItemDefinition(
                "grenade",
                "snowball",
                Component.text("Grenade"),
                List.of(),
                Map.of("power", 4.0, "fuse_ticks", 40, "fire", false),
                Map.of(),
                20001,
                "grenade",
                "icon.png"
        );

        assertEquals(4.0, strategy.readDouble(definition, "power", 1.0));
        assertEquals(40, strategy.readInt(definition, "fuse_ticks", 1));
        assertFalse(strategy.readBoolean(definition, "fire", true));
        assertTrue(strategy.readBoolean(definition, "missing", true));
    }

    private static final class TestStrategy extends AbstractIgnisItemStrategy {
        private TestStrategy() {
            super(new IgnisStrategyContext(null, null, null, null, null, null, null, null,
                    NoopExtensionSupport.INSTANCE, NoopEventBus.INSTANCE));
        }

        double readDouble(ItemDefinition definition, String key, double defaultValue) {
            return getCustomDouble(definition, key, defaultValue);
        }

        int readInt(ItemDefinition definition, String key, int defaultValue) {
            return getCustomInt(definition, key, defaultValue);
        }

        boolean readBoolean(ItemDefinition definition, String key, boolean defaultValue) {
            return getCustomBoolean(definition, key, defaultValue);
        }
    }
}
