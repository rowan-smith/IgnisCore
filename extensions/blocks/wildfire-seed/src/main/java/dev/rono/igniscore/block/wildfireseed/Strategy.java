package dev.rono.igniscore.block.wildfireseed;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        WildfireSeedRuntime runtime = new WildfireSeedRuntime(context);
        context.eventBus().subscribe(new WildfireSeedOnBlockClickListener());
        context.eventBus().subscribe(new WildfireSeedOnBlockTriggerListener(runtime));
    }

}
