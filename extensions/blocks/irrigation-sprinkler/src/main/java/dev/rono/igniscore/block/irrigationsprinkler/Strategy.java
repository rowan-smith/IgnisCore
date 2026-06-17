package dev.rono.igniscore.block.irrigationsprinkler;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new IrrigationSprinklerOnBlockClickListener());
        IrrigationSprinklerRuntime runtime = new IrrigationSprinklerRuntime(context);
        context.eventBus().subscribe(new IrrigationSprinklerOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new IrrigationSprinklerOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new IrrigationSprinklerOnBlockInteractListener(runtime));
    }

}
