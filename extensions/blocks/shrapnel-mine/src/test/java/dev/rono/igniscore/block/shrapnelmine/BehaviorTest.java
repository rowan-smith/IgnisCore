package dev.rono.igniscore.block.shrapnelmine;

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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

class BehaviorTest {
    @Test
    void placedArmsMine() {
        TestEventBus.TestContext ctx = TestEventBus.createContext();
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(BehaviorTest.class, "shrapnel-mine", 10001);
        Strategy strategy = TestEventBus.activate(() -> new Strategy(ctx.context()), "shrapnel-mine");

        assertDoesNotThrow(() -> ctx.eventBus().fireBlockPlace(
                new BlockPlaceEvent(PlacedBlock.of(definition, new IgnisLocation("world", 1, 2, 3)), null),
                "shrapnel-mine"));
    }

    @Test
    void triggerLaunchesDebris() {
        TestEventBus.TestContext ctx = TestEventBus.createContext();
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(BehaviorTest.class, "shrapnel-mine", 10001);
        Strategy strategy = TestEventBus.activate(() -> new Strategy(ctx.context()), "shrapnel-mine");
        RuntimeBlockInstance instance = BehaviorTestSupport.blockInstance(definition);

        ctx.eventBus().fireBlockTrigger(new BlockTriggerEvent(instance, null), "shrapnel-mine");

        assertFalse(ctx.world().explosions().isEmpty());
    }
}
