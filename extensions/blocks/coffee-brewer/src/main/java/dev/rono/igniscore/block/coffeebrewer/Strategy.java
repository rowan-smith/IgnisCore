package dev.rono.igniscore.block.coffeebrewer;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.CustomBlockAction;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
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
