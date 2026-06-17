package dev.rono.igniscore.block.fortunecookiemaker;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new FortuneCookieMakerOnBlockClickListener());
        FortuneCookieMakerRuntime runtime = new FortuneCookieMakerRuntime(context);
        context.eventBus().subscribe(new FortuneCookieMakerOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new FortuneCookieMakerOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new FortuneCookieMakerOnBlockInteractListener(runtime));
    }

}
