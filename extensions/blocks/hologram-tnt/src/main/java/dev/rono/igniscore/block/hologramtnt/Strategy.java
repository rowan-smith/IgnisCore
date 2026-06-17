package dev.rono.igniscore.block.hologramtnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        HologramTntRuntime runtime = new HologramTntRuntime(context);
        context.eventBus().subscribe(new HologramTntOnBlockClickListener());
        context.eventBus().subscribe(new HologramTntOnBlockTickListener(runtime));
        context.eventBus().subscribe(new HologramTntOnBlockTriggerListener(runtime));
    }

}
