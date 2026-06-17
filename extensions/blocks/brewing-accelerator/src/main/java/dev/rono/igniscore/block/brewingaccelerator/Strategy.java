package dev.rono.igniscore.block.brewingaccelerator;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new BrewingAcceleratorOnBlockClickListener());
        context.eventBus().subscribe(new BrewingAcceleratorOnBlockPlaceListener(context));
        context.eventBus().subscribe(new BrewingAcceleratorOnBlockBreakListener());
    }

}
