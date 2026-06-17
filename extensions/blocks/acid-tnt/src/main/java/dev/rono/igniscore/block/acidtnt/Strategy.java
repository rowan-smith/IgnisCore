package dev.rono.igniscore.block.acidtnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        AcidTntRuntime runtime = new AcidTntRuntime(context);
        context.eventBus().subscribe(new AcidTntOnBlockClickListener());
        context.eventBus().subscribe(new AcidTntOnBlockTickListener(runtime));
        context.eventBus().subscribe(new AcidTntOnBlockTriggerListener(runtime));
    }

}
