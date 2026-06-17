package dev.rono.igniscore.block.embermine;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        EmberMineRuntime runtime = new EmberMineRuntime(context);
        context.eventBus().subscribe(new EmberMineOnBlockClickListener());
        context.eventBus().subscribe(new EmberMineOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new EmberMineOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new EmberMineOnBlockTriggerListener(runtime));
    }

}
