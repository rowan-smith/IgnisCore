package dev.rono.igniscore.block.scarecharge;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        ScareChargeRuntime runtime = new ScareChargeRuntime(context);
        context.eventBus().subscribe(new ScareChargeOnBlockClickListener());
        context.eventBus().subscribe(new ScareChargeOnBlockTriggerListener(runtime));
    }

}
