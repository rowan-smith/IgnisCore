package dev.rono.igniscore.block.spicerack;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.CustomBlockAction;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        SpiceRackRuntime runtime = new SpiceRackRuntime(context);
        context.eventBus().subscribe(new SpiceRackOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new SpiceRackOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new SpiceRackOnBlockInteractListener(runtime));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.placed();
    }

}
