package dev.rono.igniscore.block.swapcharge;

import dev.rono.extensions.shared.strategy.PlacedClickListener;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(PlacedClickListener.combustible());
        context.eventBus().subscribe(new SwapChargeOnBlockTickListener(context));
        context.eventBus().subscribe(new SwapChargeOnBlockTriggerListener(context));
    }

}
