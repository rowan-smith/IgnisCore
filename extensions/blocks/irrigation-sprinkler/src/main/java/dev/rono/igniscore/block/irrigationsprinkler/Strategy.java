package dev.rono.igniscore.block.irrigationsprinkler;

import dev.rono.extensions.shared.strategy.PlacedClickListener;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(PlacedClickListener.fixed(CustomBlockAction.BREAK, CustomBlockAction.OPEN));
        IrrigationSprinklerRuntime runtime = new IrrigationSprinklerRuntime(context);
        context.eventBus().subscribe(new IrrigationSprinklerOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new IrrigationSprinklerOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new IrrigationSprinklerOnBlockInteractListener(runtime));
    }

}
