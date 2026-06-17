package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;

public class Strategy extends AbstractIgnisBlockStrategy {
    public Strategy(IgnisStrategyContext context) {
        super(context);
        QuarryCacheRuntime runtime = new QuarryCacheRuntime(context);
        context.eventBus().subscribe(new QuarryCacheOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new QuarryCacheOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new QuarryCacheOnBlockInteractListener(runtime));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.placed();
    }
}
