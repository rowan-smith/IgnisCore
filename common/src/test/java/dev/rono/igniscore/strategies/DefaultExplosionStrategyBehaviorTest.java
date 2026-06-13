package dev.rono.igniscore.strategies;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.testsupport.BehaviorTestSupport;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DefaultExplosionStrategyBehaviorTest {

    @Test
    void triggerStoresBlastPowerAndExplodes() {
        BehaviorTestSupport.TestContext ctx = BehaviorTestSupport.createContext();
        DefaultExplosionStrategy strategy = new DefaultExplosionStrategy(ctx.context().getExtensionSupport());
        RuntimeBlockInstance instance = BehaviorTestSupport.blockInstance(sampleDefinition());

        strategy.onTrigger(instance, null);

        assertEquals(4.0, instance.getData().getDouble("ignis:blast_power"));
        assertFalse(ctx.world().explosions().isEmpty());
        assertFalse(ctx.world().sounds().isEmpty());
    }

    @Test
    void triggerUsesCustomPowerFromDefinition() {
        BehaviorTestSupport.TestContext ctx = BehaviorTestSupport.createContext();
        DefaultExplosionStrategy strategy = new DefaultExplosionStrategy(ctx.context().getExtensionSupport());
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

        strategy.onTrigger(instance, null);

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
