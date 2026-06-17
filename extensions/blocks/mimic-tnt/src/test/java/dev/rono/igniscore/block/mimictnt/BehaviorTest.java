package dev.rono.igniscore.block.mimictnt;

import dev.rono.igniscore.api.event.BlockActivateEvent;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.testsupport.BehaviorTestSupport;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import dev.rono.igniscore.testsupport.TestEventBus;
import org.junit.jupiter.api.Test;

class BehaviorTest {
    @Test
    void activateDoesNotThrow() {
        TestEventBus.TestContext ctx = TestEventBus.createContext();
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(BehaviorTest.class, "mimic-tnt", 10001);
        Strategy strategy = TestEventBus.activate(() -> new Strategy(ctx.context()), "mimic-tnt");
        RuntimeBlockInstance instance = BehaviorTestSupport.blockInstance(definition);

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                ctx.eventBus().fireBlockActivate(new BlockActivateEvent(instance), "mimic-tnt"));
    }
}
