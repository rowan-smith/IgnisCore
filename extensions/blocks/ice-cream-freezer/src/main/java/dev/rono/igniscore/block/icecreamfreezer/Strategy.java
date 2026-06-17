package dev.rono.igniscore.block.icecreamfreezer;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.CustomBlockAction;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        IceCreamFreezerRuntime runtime = new IceCreamFreezerRuntime(context);
        context.eventBus().subscribe(new IceCreamFreezerOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new IceCreamFreezerOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new IceCreamFreezerOnBlockInteractListener(runtime));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.placed();
    }

}
