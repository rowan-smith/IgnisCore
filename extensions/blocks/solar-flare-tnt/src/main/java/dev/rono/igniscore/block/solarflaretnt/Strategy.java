package dev.rono.igniscore.block.solarflaretnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        SolarFlareTntRuntime runtime = new SolarFlareTntRuntime(context);
        context.eventBus().subscribe(new SolarFlareTntOnBlockClickListener());
        context.eventBus().subscribe(new SolarFlareTntOnBlockTickListener(runtime));
        context.eventBus().subscribe(new SolarFlareTntOnBlockTriggerListener(runtime));
    }

}
