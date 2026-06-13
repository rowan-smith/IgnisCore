package dev.rono.igniscore.block.eruptingtnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.testsupport.BehaviorTestSupport;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class BehaviorTest {
    @Test
    void triggerCreatesExplosion() {
        BehaviorTestSupport.TestContext ctx = BehaviorTestSupport.createContext();
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(BehaviorTest.class, "erupting-tnt", 10001);
        Strategy strategy = new Strategy(ctx.context());
        RuntimeBlockInstance instance = BehaviorTestSupport.blockInstance(definition);

        strategy.onTrigger(instance, null);

        assertFalse(ctx.world().explosions().isEmpty());
    }
}
