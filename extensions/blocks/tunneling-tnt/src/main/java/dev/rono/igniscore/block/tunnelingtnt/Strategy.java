package dev.rono.igniscore.block.tunnelingtnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new TunnelingOnBlockClickListener());
        context.eventBus().subscribe(new TunnelingOnBlockTriggerListener(context));
    }

}
