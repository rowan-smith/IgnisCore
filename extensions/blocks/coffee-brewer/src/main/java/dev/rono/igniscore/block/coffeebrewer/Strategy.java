package dev.rono.igniscore.block.coffeebrewer;

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
        CoffeeBrewerRuntime runtime = new CoffeeBrewerRuntime(context);
        context.eventBus().subscribe(new CoffeeBrewerOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new CoffeeBrewerOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new CoffeeBrewerOnBlockInteractListener(runtime));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.placed();
    }

}
