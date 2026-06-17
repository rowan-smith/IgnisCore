package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.config.BlockBehaviorConfig;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.PlacedClickSupport;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class BehaviorTest {
    @Test
    void rightClickOpensGui() {
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(BehaviorTest.class, "quarry-cache", 10001);

        assertEquals(CustomBlockAction.OPEN,
                PlacedClickSupport.resolve(definition, CustomBlockAction.BREAK, CustomBlockAction.OPEN,
                        IgnisInteraction.RIGHT_CLICK_BLOCK, "AIR"));
        assertFalse(BlockBehaviorConfig.from(definition.getBehaviorConfig()).combustible());
    }
}
