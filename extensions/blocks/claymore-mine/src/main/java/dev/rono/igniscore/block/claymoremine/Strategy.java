package dev.rono.igniscore.block.claymoremine;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        ClaymoreMineRuntime runtime = new ClaymoreMineRuntime(context);
        context.eventBus().subscribe(new ClaymoreMineOnBlockClickListener());
        context.eventBus().subscribe(new ClaymoreMineOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new ClaymoreMineOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new ClaymoreMineOnBlockTriggerListener(runtime));
    }

}
