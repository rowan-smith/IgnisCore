package dev.rono.igniscore.block.powdertrail;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final PowderTrailBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new PowderTrailBehavior(context);
        onBlockTick(event -> behavior.onTick(event.instance()));
        onBlockTrigger(event -> behavior.onTrigger(event.instance(), event.triggerContext()));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder()
                .defaultFuse(0)
                .build();
    }

}
