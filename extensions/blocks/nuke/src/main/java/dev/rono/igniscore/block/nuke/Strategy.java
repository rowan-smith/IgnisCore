package dev.rono.igniscore.block.nuke;

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
        context.eventBus().subscribe(new NukeOnBlockPlaceListener(context));
        context.eventBus().subscribe(new NukeOnBlockActivateListener(context));
        context.eventBus().subscribe(new NukeOnBlockTickListener(context));
        context.eventBus().subscribe(new NukeOnBlockTriggerListener(context));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.combustible(
                dev.rono.igniscore.api.strategy.StrategySupport.customInt(definition, "fuse", 160),
                dev.rono.igniscore.api.strategy.StrategySupport.customDouble(definition, "radius", 30.0));
    }

}
