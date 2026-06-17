package dev.rono.igniscore.block.glitchtnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        GlitchTntRuntime runtime = new GlitchTntRuntime(context);
        context.eventBus().subscribe(new GlitchTntOnBlockClickListener());
        context.eventBus().subscribe(new GlitchTntOnBlockTickListener(runtime));
        context.eventBus().subscribe(new GlitchTntOnBlockTriggerListener(runtime));
    }

}
