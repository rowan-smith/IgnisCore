package dev.rono.igniscore.item.detonator;

import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.testsupport.BehaviorTestSupport;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BehaviorTest {
    @Test
    void strategyConstructsWithBehaviorPipeline() {
        ItemDefinition definition = ExtensionTestSupport.loadItemDefinition(BehaviorTest.class, "detonator", 20001);
        Strategy strategy = new Strategy(BehaviorTestSupport.createContext().context());

        assertNotNull(strategy);
        assertNotNull(definition.getCustomData());
    }
}
