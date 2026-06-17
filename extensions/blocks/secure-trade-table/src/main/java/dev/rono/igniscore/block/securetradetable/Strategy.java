package dev.rono.igniscore.block.securetradetable;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new SecureTradeTableOnBlockClickListener());
        SecureTradeTableRuntime runtime = new SecureTradeTableRuntime(context);
        context.eventBus().subscribe(new SecureTradeTableOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new SecureTradeTableOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new SecureTradeTableOnBlockInteractListener(runtime));
    }

}
