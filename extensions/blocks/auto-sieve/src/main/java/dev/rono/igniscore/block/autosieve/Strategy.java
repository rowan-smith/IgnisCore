package dev.rono.igniscore.block.autosieve;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final AutoSieveBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new AutoSieveBehavior(context);
        context.eventBus().subscribe(new AutoSieveOnBlockPlaceListener(behavior));
        context.eventBus().subscribe(new AutoSieveOnBlockBreakListener(behavior));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.defaults();
    }
}
