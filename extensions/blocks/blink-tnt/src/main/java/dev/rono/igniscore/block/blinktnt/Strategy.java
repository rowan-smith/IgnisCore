package dev.rono.igniscore.block.blinktnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new BlinkTntOnBlockClickListener());
        context.eventBus().subscribe(new BlinkTntOnBlockTickListener(context));
        context.eventBus().subscribe(new BlinkTntOnBlockTriggerListener(context));
    }

}
