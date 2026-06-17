package dev.rono.igniscore.block.bridgebuilder;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.extensions.shared.strategy.PlacedClickListener;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(PlacedClickListener.forStrategy(this));
        context.eventBus().subscribe(new BridgeBuilderOnBlockTickListener(context));
        context.eventBus().subscribe(new BridgeBuilderOnBlockTriggerListener(context));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.combustible(
                dev.rono.igniscore.api.strategy.StrategySupport.customInt(definition, "fuse", 80),
                dev.rono.igniscore.api.strategy.StrategySupport.customDouble(definition, "radius", 4.0));
    }

}
