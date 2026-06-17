package dev.rono.igniscore.block.spicerack;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new SpiceRackOnBlockClickListener());
        SpiceRackRuntime runtime = new SpiceRackRuntime(context);
        context.eventBus().subscribe(new SpiceRackOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new SpiceRackOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new SpiceRackOnBlockInteractListener(runtime));
    }

}
