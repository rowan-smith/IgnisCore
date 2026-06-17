package dev.rono.igniscore.block.mimictnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new MimicOnBlockClickListener());
        context.eventBus().subscribe(new MimicOnBlockActivateListener(context));
    }

}
