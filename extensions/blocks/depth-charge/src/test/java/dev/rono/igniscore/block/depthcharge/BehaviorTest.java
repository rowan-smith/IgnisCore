package dev.rono.igniscore.block.depthcharge;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.testsupport.BehaviorTestSupport;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import dev.rono.igniscore.testsupport.TestEventBus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

class BehaviorTest {
    @Test
    void triggerBreaksUnderwater() {
        TestEventBus.TestContext ctx = TestEventBus.createContext();
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(BehaviorTest.class, "depth-charge", 10001);
        Strategy strategy = TestEventBus.activate(() -> new Strategy(ctx.context()), "depth-charge");
        RuntimeBlockInstance instance = BehaviorTestSupport.blockInstance(definition);
        assertDoesNotThrow(() -> ctx.eventBus().fireBlockTrigger(new BlockTriggerEvent(instance, null), "depth-charge"));
        assertFalse(ctx.world().explosions().isEmpty());
    }
}
