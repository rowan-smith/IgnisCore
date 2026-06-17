package dev.rono.igniscore.strategies;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.event.IgnisEventBusImpl;
import dev.rono.igniscore.testsupport.BehaviorTestSupport;
import dev.rono.igniscore.support.NoopExtensionSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultExplosionStrategyTest {
    @Test
    void exposesDefaultDescriptor() {
        DefaultExplosionStrategy strategy = new DefaultExplosionStrategy(
                NoopExtensionSupport.INSTANCE, new IgnisEventBusImpl());

        assertEquals("default", strategy.descriptor().getId());
        assertEquals("Default Explosion", strategy.descriptor().getName());
        assertEquals("IgnisCore", strategy.descriptor().getAuthor());
    }

    @Test
    void usesExplosiveProfileDefaults() {
        DefaultExplosionStrategy strategy = new DefaultExplosionStrategy(
                NoopExtensionSupport.INSTANCE, new IgnisEventBusImpl());
        StrategyProfile profile = strategy.profile(sampleDefinition());

        assertTrue(profile.isCombustible());
        assertEquals(CustomBlockAction.BREAK, profile.getLeftClickAction());
        assertEquals(CustomBlockAction.IGNITE, profile.getRightClickAction());
        assertEquals(80, profile.getDefaultFuse());
        assertEquals(4.0, profile.getDefaultRadius());
        assertEquals("BLOCK_BEACON_ACTIVATE", profile.getPlacementSound());
    }

    @Test
    void triggerStoresBlastPowerAndExplodes() {
        IgnisEventBusImpl eventBus = new IgnisEventBusImpl();
        BehaviorTestSupport.TestContext ctx = BehaviorTestSupport.createContext(eventBus);
        DefaultExplosionStrategy strategy = new DefaultExplosionStrategy(ctx.context().extensions(), eventBus);
        RuntimeBlockInstance instance = BehaviorTestSupport.blockInstance(sampleDefinition());

        eventBus.dispatch("default", dev.rono.igniscore.api.event.OnBlockTriggerListener.class,
                listener -> listener.onBlockTrigger(new BlockTriggerEvent(instance, null)));

        assertEquals(4.0, instance.getData().getDouble("ignis:blast_power"));
        org.junit.jupiter.api.Assertions.assertFalse(ctx.world().explosions().isEmpty());
    }

    private BlockDefinition sampleDefinition() {
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
                false
        );
    }
}
