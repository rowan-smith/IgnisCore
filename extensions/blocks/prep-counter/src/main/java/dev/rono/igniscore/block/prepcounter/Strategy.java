package dev.rono.igniscore.block.prepcounter;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.CustomBlockAction;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        PrepCounterRuntime runtime = new PrepCounterRuntime(context);
        context.eventBus().subscribe(new PrepCounterOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new PrepCounterOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new PrepCounterOnBlockInteractListener(runtime));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.placed();
    }

}
