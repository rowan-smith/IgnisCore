package dev.rono.igniscore.block.ouijaslab;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new OuijaSlabOnBlockClickListener());
        context.eventBus().subscribe(new OuijaSlabOnBlockPlaceListener(context));
        context.eventBus().subscribe(new OuijaSlabOnBlockBreakListener());
    }

}
