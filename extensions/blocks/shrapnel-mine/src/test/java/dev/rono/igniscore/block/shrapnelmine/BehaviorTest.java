package dev.rono.igniscore.block.shrapnelmine;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.testsupport.BehaviorTestSupport;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

class BehaviorTest {
    @Test
    void placedArmsMine() {
        BehaviorTestSupport.TestContext ctx = BehaviorTestSupport.createContext();
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(BehaviorTest.class, "shrapnel-mine", 10001);
        Strategy strategy = new Strategy(ctx.context());
        assertDoesNotThrow(() -> strategy.onPlaced(definition, new IgnisLocation("world", 1, 2, 3)));
    }

    @Test
    void triggerLaunchesDebris() {
        BehaviorTestSupport.TestContext ctx = BehaviorTestSupport.createContext();
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(BehaviorTest.class, "shrapnel-mine", 10001);
        Strategy strategy = new Strategy(ctx.context());
        RuntimeBlockInstance instance = BehaviorTestSupport.blockInstance(definition);
        strategy.onTrigger(instance, null);
        assertFalse(ctx.world().explosions().isEmpty());
    }
}
