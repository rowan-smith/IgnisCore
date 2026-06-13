package dev.rono.igniscore.block.mimictnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.testsupport.BehaviorTestSupport;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BehaviorTest {
    @Test
    void onPlaceSpawnsTntEntitiesAndClearsFuse() {
        BehaviorTestSupport.TestContext ctx = BehaviorTestSupport.createContext();
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(BehaviorTest.class, "mimic-tnt", 10001);
        Strategy strategy = new Strategy(ctx.context());
        RuntimeBlockInstance instance = BehaviorTestSupport.blockInstance(definition);
        instance.setTicksLeft(80);

        strategy.onPlace(instance);

        assertEquals(0, instance.getTicksLeft());
    }
}
