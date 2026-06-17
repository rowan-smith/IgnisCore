package dev.rono.igniscore.block.tsunamicharge;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        TsunamiChargeRuntime runtime = new TsunamiChargeRuntime(context);
        context.eventBus().subscribe(new TsunamiChargeOnBlockClickListener());
        context.eventBus().subscribe(new TsunamiChargeOnBlockTickListener(runtime));
        context.eventBus().subscribe(new TsunamiChargeOnBlockTriggerListener(runtime));
    }

}
