package dev.rono.igniscore.block.securetradetable;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.CustomBlockAction;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        SecureTradeTableRuntime runtime = new SecureTradeTableRuntime(context);
        context.eventBus().subscribe(new SecureTradeTableOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new SecureTradeTableOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new SecureTradeTableOnBlockInteractListener(runtime));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.placed();
    }

}
