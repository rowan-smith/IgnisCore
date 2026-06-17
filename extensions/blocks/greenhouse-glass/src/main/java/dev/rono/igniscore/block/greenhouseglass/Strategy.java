package dev.rono.igniscore.block.greenhouseglass;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new GreenhouseGlassOnBlockClickListener());
        context.eventBus().subscribe(new GreenhouseGlassOnBlockPlaceListener(context));
        context.eventBus().subscribe(new GreenhouseGlassOnBlockBreakListener());
    }

}
