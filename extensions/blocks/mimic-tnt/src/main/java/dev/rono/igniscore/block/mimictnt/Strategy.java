package dev.rono.igniscore.block.mimictnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final MimicBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new MimicBehavior(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder().build();
    }

    @Override
    public void onPlace(RuntimeBlockInstance instance) {
        behavior.onPlace(instance, instance.getDefinition());
    }
}
