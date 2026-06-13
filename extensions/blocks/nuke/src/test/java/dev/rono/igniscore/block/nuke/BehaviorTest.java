package dev.rono.igniscore.block.nuke;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.testsupport.BehaviorTestSupport;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BehaviorTest {
    @Test
    void staticPlaceSpawnsParticles() {
        BehaviorTestSupport.TestContext ctx = BehaviorTestSupport.createContext();
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(BehaviorTest.class, "nuke", 10001);
        Strategy strategy = new Strategy(ctx.context());

        strategy.onStaticPlace(definition, new IgnisLocation("world", 1, 2, 3));

        assertFalse(ctx.world().particles().isEmpty());
    }

    @Test
    void triggerStoresPowerAndExplodes() {
        BehaviorTestSupport.TestContext ctx = BehaviorTestSupport.createContext();
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(BehaviorTest.class, "nuke", 10001);
        Strategy strategy = new Strategy(ctx.context());
        RuntimeBlockInstance instance = BehaviorTestSupport.blockInstance(definition);

        strategy.onTrigger(instance, null);

        assertTrue(instance.getData().getDouble("ignis:nuke_power") > 0);
        assertFalse(ctx.world().explosions().isEmpty());
    }
}
