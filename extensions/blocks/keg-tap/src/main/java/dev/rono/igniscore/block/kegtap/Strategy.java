package dev.rono.igniscore.block.kegtap;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.CustomBlockAction;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        KegTapRuntime runtime = new KegTapRuntime(context);
        context.eventBus().subscribe(new KegTapOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new KegTapOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new KegTapOnBlockInteractListener(runtime));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.placed();
    }

}
