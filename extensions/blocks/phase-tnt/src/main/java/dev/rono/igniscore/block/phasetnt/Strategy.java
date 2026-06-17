package dev.rono.igniscore.block.phasetnt;

import dev.rono.extensions.shared.strategy.PlacedClickListener;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(PlacedClickListener.combustible());
        context.eventBus().subscribe(new PhaseTntOnBlockTickListener(context));
        context.eventBus().subscribe(new PhaseTntOnBlockTriggerListener(context));
    }

}
