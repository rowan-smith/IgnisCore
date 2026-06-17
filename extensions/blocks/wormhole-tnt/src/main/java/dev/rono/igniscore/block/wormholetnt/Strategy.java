package dev.rono.igniscore.block.wormholetnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.extensions.shared.strategy.StrategyProfiles;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final WormholeBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new WormholeBehavior(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfiles.explosiveProfile().toBuilder()
                .defaultFuse(100)
                .defaultRadius(12.0)
                .build();
    }

    @Override
    public void onTick(RuntimeBlockInstance instance) {
        behavior.onTick(instance);
    }

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        behavior.onTrigger(instance);
    }
}
