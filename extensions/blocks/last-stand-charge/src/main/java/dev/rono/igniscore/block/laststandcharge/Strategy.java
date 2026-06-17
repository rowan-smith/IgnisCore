package dev.rono.igniscore.block.laststandcharge;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final LastStandChargeBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new LastStandChargeBehavior(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder()
                .defaultFuse(0).combustible(false)
                .build();
    }

    @Override
    public void onTick(RuntimeBlockInstance instance) {
        behavior.onTick(instance);
    }

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        behavior.onTrigger(instance, triggerContext);
    }
}
