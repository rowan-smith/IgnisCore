package dev.rono.igniscore.block.bridgebuilder;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new BridgeBuilderOnBlockClickListener());
        context.eventBus().subscribe(new BridgeBuilderOnBlockTickListener(context));
        context.eventBus().subscribe(new BridgeBuilderOnBlockTriggerListener(context));
    }

}
