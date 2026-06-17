package dev.rono.igniscore.block.tethertnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        TetherTntRuntime runtime = new TetherTntRuntime(context);
        context.eventBus().subscribe(new TetherTntOnBlockClickListener());
        context.eventBus().subscribe(new TetherTntOnBlockTickListener(runtime));
        context.eventBus().subscribe(new TetherTntOnBlockTriggerListener(runtime));
    }

}
