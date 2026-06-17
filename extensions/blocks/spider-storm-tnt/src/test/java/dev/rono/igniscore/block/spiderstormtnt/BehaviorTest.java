package dev.rono.igniscore.block.spiderstormtnt;

import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.testsupport.BehaviorTestSupport;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import dev.rono.igniscore.testsupport.TestEventBus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class BehaviorTest {
    @Test
    void placedSpawnsParticles() {
        TestEventBus.TestContext ctx = TestEventBus.createContext();
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(BehaviorTest.class, "spider-storm-tnt", 10001);
        Strategy strategy = new Strategy(ctx.context());
        TestEventBus.activate(strategy, "spider-storm-tnt");

        ctx.eventBus().fireBlockPlace(new BlockPlaceEvent(definition, new IgnisLocation("world", 1, 2, 3), null), "spider-storm-tnt");

        assertFalse(ctx.world().particles().isEmpty());
    }

    @Test
    void triggerCreatesBurst() {
        TestEventBus.TestContext ctx = TestEventBus.createContext();
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(BehaviorTest.class, "spider-storm-tnt", 10001);
        Strategy strategy = new Strategy(ctx.context());
        TestEventBus.activate(strategy, "spider-storm-tnt");
        RuntimeBlockInstance instance = BehaviorTestSupport.blockInstance(definition);

        ctx.eventBus().fireBlockTrigger(new BlockTriggerEvent(instance, null), "spider-storm-tnt");

        assertFalse(ctx.world().particles().isEmpty());
    }
}
