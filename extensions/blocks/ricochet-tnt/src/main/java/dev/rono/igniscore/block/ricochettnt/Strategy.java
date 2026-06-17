package dev.rono.igniscore.block.ricochettnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new RicochetTntOnBlockClickListener());
        context.eventBus().subscribe(new RicochetTntOnBlockTickListener(context));
        context.eventBus().subscribe(new RicochetTntOnBlockTriggerListener(context));
    }

}
