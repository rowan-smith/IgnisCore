package dev.rono.igniscore.block.nuke;

import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.PlacedBlock;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.testsupport.BehaviorTestSupport;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import dev.rono.igniscore.testsupport.TestEventBus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BehaviorTest {
    @Test
    void placedSpawnsParticles() {
        TestEventBus.TestContext ctx = TestEventBus.createContext();
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(BehaviorTest.class, "nuke", 10001);
        Strategy strategy = TestEventBus.activate(() -> new Strategy(ctx.context()), "nuke");

        ctx.eventBus().fireBlockPlace(
                new BlockPlaceEvent(PlacedBlock.of(definition, new IgnisLocation("world", 1, 2, 3)), null),
                "nuke");

        assertFalse(ctx.world().particles().isEmpty());
    }

    @Test
    void triggerStoresPowerAndExplodes() {
        TestEventBus.TestContext ctx = TestEventBus.createContext();
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(BehaviorTest.class, "nuke", 10001);
        Strategy strategy = TestEventBus.activate(() -> new Strategy(ctx.context()), "nuke");
        RuntimeBlockInstance instance = BehaviorTestSupport.blockInstance(definition);

        ctx.eventBus().fireBlockTrigger(new BlockTriggerEvent(instance, null), "nuke");

        assertTrue(instance.getData().getDouble("ignis:nuke_power") > 0);
        assertFalse(ctx.world().explosions().isEmpty());
    }
}
