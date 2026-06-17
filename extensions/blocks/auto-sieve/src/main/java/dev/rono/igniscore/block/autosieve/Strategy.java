package dev.rono.igniscore.block.autosieve;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {
    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new AutoSieveOnBlockPlaceListener(context));
        context.eventBus().subscribe(new AutoSieveOnBlockBreakListener());
        context.eventBus().subscribe(new AutoSieveOnBlockClickListener());
    }
}
