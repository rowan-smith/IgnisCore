package dev.rono.igniscore.block.lostandfoundbin;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.CustomBlockAction;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        LostAndFoundBinRuntime runtime = new LostAndFoundBinRuntime(context);
        context.eventBus().subscribe(new LostAndFoundBinOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new LostAndFoundBinOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new LostAndFoundBinOnBlockInteractListener(runtime));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.placed();
    }

}
