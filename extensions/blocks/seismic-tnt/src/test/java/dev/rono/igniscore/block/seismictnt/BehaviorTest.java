package dev.rono.igniscore.block.seismictnt;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.testsupport.BehaviorTestSupport;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import dev.rono.igniscore.testsupport.TestEventBus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class BehaviorTest {
    @Test
    void triggerStartsSeismicWave() {
        TestEventBus.TestContext ctx = TestEventBus.createContext();
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(BehaviorTest.class, "seismic-tnt", 10001);
        Strategy strategy = TestEventBus.activate(() -> new Strategy(ctx.context()), "seismic-tnt");
        RuntimeBlockInstance instance = BehaviorTestSupport.blockInstance(definition);
        assertDoesNotThrow(() -> ctx.eventBus().fireBlockTrigger(new BlockTriggerEvent(instance, null), "seismic-tnt"));
    }
}
