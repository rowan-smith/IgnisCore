package dev.rono.igniscore.strategies;

import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.IgnisEventBus;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.testsupport.BehaviorTestSupport;
import dev.rono.igniscore.testsupport.TestEventBus;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DefaultExplosionStrategyBehaviorTest {

    @Test
    void triggerStoresBlastPowerAndExplodes() {
        TestEventBus eventBus = new TestEventBus();
        BehaviorTestSupport.TestContext ctx = BehaviorTestSupport.createContext(eventBus);
        DefaultExplosionStrategy strategy = new DefaultExplosionStrategy(ctx.context().getExtensionSupport(), eventBus);
        strategy.registerEvents();
        RuntimeBlockInstance instance = BehaviorTestSupport.blockInstance(sampleDefinition());

        eventBus.fireBlockTrigger(new BlockTriggerEvent(instance, null), "default");

        assertEquals(4.0, instance.getData().getDouble("ignis:blast_power"));
        assertFalse(ctx.world().explosions().isEmpty());
        assertFalse(ctx.world().sounds().isEmpty());
    }

    @Test
    void triggerUsesCustomPowerFromDefinition() {
        IgnisEventBus eventBus = new TestEventBus();
        BehaviorTestSupport.TestContext ctx = BehaviorTestSupport.createContext(eventBus);
        DefaultExplosionStrategy strategy = new DefaultExplosionStrategy(ctx.context().getExtensionSupport(), eventBus);
        strategy.registerEvents();
        BlockDefinition powered = new BlockDefinition(
                "powered",
                "paper",
                "carrot_on_a_stick",
                Component.text("Powered"),
                List.of(),
                true,
                true,
                "top.png",
                "side.png",
                "bottom.png",
                Map.of("power", 9.0, "radius", 9.0),
                Map.of(),
                Map.of(),
                Map.of(),
                10002,
                false,
                false,
                false,
                "powered");
        RuntimeBlockInstance instance = BehaviorTestSupport.blockInstance(powered);

        ((TestEventBus) eventBus).fireBlockTrigger(new BlockTriggerEvent(instance, null), "default");

        assertEquals(9.0, instance.getData().getDouble("ignis:blast_power"));
    }

    private static BlockDefinition sampleDefinition() {
        return new BlockDefinition(
                "test",
                "paper",
                "carrot_on_a_stick",
                Component.text("Test"),
                List.of(),
                true,
                true,
                "top.png",
                "side.png",
                "bottom.png",
                Map.of("fuse", 80, "radius", 4.0),
                Map.of(),
                Map.of(),
                Map.of(),
                10001,
                false,
                false,
                false,
                "test");
    }
}
