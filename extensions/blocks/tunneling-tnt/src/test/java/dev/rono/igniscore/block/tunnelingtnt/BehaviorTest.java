package dev.rono.igniscore.block.tunnelingtnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.testsupport.BehaviorTestSupport;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class BehaviorTest {
    @Test
    void triggerSchedulesTunnelWithoutError() {
        BehaviorTestSupport.TestContext ctx = BehaviorTestSupport.createContext();
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(BehaviorTest.class, "tunneling-tnt", 10001);
        Strategy strategy = new Strategy(ctx.context());
        RuntimeBlockInstance instance = BehaviorTestSupport.blockInstance(definition);

        assertDoesNotThrow(() -> strategy.onTrigger(instance, null));
    }
}
