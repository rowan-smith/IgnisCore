package dev.rono.igniscore.block.displaycase;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.CustomBlockAction;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        DisplayCaseRuntime runtime = new DisplayCaseRuntime(context);
        context.eventBus().subscribe(new DisplayCaseOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new DisplayCaseOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new DisplayCaseOnBlockInteractListener(runtime));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.placed();
    }

}
