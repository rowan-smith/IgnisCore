package dev.rono.igniscore.block.displaycase;

import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.CustomBlockAction;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        DisplayCaseListeners listeners = new DisplayCaseListeners(context);
        context.eventBus().subscribe((OnBlockPlaceListener) listeners);
        context.eventBus().subscribe((OnBlockBreakListener) listeners);
        context.eventBus().subscribe((OnBlockInteractListener) listeners);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.defaults();
    }

}
