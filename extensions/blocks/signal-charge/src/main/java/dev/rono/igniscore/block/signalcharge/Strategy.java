package dev.rono.igniscore.block.signalcharge;

import dev.rono.extensions.shared.strategy.PlacedClickListener;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(PlacedClickListener.forStrategy(this));
        context.eventBus().subscribe(new SignalChargeOnBlockTriggerListener(context));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.combustible(
                dev.rono.igniscore.api.strategy.StrategySupport.customInt(definition, "fuse", 80),
                dev.rono.igniscore.api.strategy.StrategySupport.customDouble(definition, "radius", 4.0));
    }

}
