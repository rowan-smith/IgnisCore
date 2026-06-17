package dev.rono.igniscore.block.securetradetable;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.extensions.shared.strategy.PlacedClickListener;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(PlacedClickListener.fixed(CustomBlockAction.BREAK, CustomBlockAction.OPEN));
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
