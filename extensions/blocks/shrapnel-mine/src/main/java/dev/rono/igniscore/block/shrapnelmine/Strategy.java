package dev.rono.igniscore.block.shrapnelmine;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        ShrapnelMineRuntime runtime = new ShrapnelMineRuntime(context);
        context.eventBus().subscribe(new ShrapnelMineOnBlockClickListener());
        context.eventBus().subscribe(new ShrapnelMineOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new ShrapnelMineOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new ShrapnelMineOnBlockTriggerListener(runtime));
    }

}
