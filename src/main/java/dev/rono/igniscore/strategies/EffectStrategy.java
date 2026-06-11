package dev.rono.igniscore.strategies;

import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.core.BuiltinStrategyBootstrap;
import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.RuntimeBlockInstance;

public class EffectStrategy extends BaseBlockBehaviorStrategy {
    public EffectStrategy() {
        super(IgnisStrategyDescriptor.of("effect", "Effect Burst", "1.0.0", "IgnisCore"));
    }

    @Override
    public dev.rono.igniscore.api.strategy.StrategyProfile profile(BlockDefinition definition) {
        return BuiltinStrategyBootstrap.explosiveProfile();
    }
    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        // Placeholder for future potion effect/particle logic
    }
}
