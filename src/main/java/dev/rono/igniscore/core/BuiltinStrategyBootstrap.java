package dev.rono.igniscore.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.service.CustomBlockAction;
import dev.rono.igniscore.strategies.*;

@Singleton
public class BuiltinStrategyBootstrap {
    private final IgnisStrategyRegistry strategyRegistry;

    @Inject
    public BuiltinStrategyBootstrap(IgnisStrategyRegistry strategyRegistry) {
        this.strategyRegistry = strategyRegistry;
    }

    public void registerAll() {
        strategyRegistry.register(new DefaultExplosionStrategy());
        strategyRegistry.register(new NuclearStrategy());
        strategyRegistry.register(new EntityStrategy());
        strategyRegistry.register(new PhantomStrategy());
        strategyRegistry.register(new EruptingStrategy());
        strategyRegistry.register(new MimicStrategy());
        strategyRegistry.register(new TunnelingStrategy());
        strategyRegistry.register(new WormholeStrategy());
        strategyRegistry.register(new StructureStrategy());
        strategyRegistry.register(new EffectStrategy());
    }

    public static StrategyProfile explosiveProfile() {
        return StrategyProfile.builder()
                .combustible(true)
                .leftClickAction(CustomBlockAction.BREAK)
                .rightClickAction(CustomBlockAction.IGNITE)
                .defaultFuse(80)
                .defaultRadius(4.0)
                .placementSound("BLOCK_BEACON_ACTIVATE")
                .igniteSound("ITEM_FLINTANDSTEEL_USE")
                .displayScale(1.01)
                .build();
    }
}
