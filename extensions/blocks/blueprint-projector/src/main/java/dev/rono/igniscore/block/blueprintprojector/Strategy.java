package dev.rono.igniscore.block.blueprintprojector;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new BlueprintProjectorOnBlockClickListener());
        context.eventBus().subscribe(new BlueprintProjectorOnBlockPlaceListener(context));
        context.eventBus().subscribe(new BlueprintProjectorOnBlockBreakListener());
    }

}
