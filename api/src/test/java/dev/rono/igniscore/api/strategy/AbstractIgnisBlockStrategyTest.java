package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.model.BlockDefinition;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractIgnisBlockStrategyTest {
    @Test
    void readsCustomDataHelpers() {
        TestStrategy strategy = new TestStrategy();
        BlockDefinition definition = new BlockDefinition(
                "test",
                "paper",
                "carrot_on_a_stick",
                Component.text("Test"),
                List.of(),
                true,
                true,
                "top.png",
                "side.png",
                "bottom.png",
                Map.of("power", 7.5, "armed", true, "waves", 3, "fuse", 80, "radius", 4.0),
                Map.of(),
                Map.of(),
                Map.of(),
                10001,
                false,
                false,
                false
        );

        assertEquals(7.5, strategy.readDouble(definition, "power", 1.0));
        assertEquals(1.0, strategy.readDouble(definition, "missing", 1.0));
        assertEquals(3, strategy.readInt(definition, "waves", 1));
        assertTrue(strategy.readBoolean(definition, "armed", false));
        assertFalse(strategy.readBoolean(definition, "missing", false));
    }

    private static final class TestStrategy extends AbstractIgnisBlockStrategy {
        private TestStrategy() {
            super(IgnisStrategyDescriptor.of("test", "Test", "1.0.0", "Tests"));
        }

        double readDouble(BlockDefinition definition, String key, double defaultValue) {
            return getCustomDouble(definition, key, defaultValue);
        }

        int readInt(BlockDefinition definition, String key, int defaultValue) {
            return getCustomInt(definition, key, defaultValue);
        }

        boolean readBoolean(BlockDefinition definition, String key, boolean defaultValue) {
            return getCustomBoolean(definition, key, defaultValue);
        }
    }
}
