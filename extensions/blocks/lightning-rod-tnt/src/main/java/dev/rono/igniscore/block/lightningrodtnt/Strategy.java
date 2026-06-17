package dev.rono.igniscore.block.lightningrodtnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        LightningRodTntRuntime runtime = new LightningRodTntRuntime(context);
        context.eventBus().subscribe(new LightningRodTntOnBlockClickListener());
        context.eventBus().subscribe(new LightningRodTntOnBlockTickListener(runtime));
        context.eventBus().subscribe(new LightningRodTntOnBlockTriggerListener(runtime));
    }

}
