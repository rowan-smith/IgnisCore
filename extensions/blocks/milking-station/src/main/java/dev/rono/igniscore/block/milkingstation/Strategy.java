package dev.rono.igniscore.block.milkingstation;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final MilkingStationBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new MilkingStationBehavior(context);
        onBlockPlace(event -> behavior.onPlaced(event.definition(), event.block()));
        onBlockBreak(event -> behavior.onPlacedBreak(event.definition(), event.block()));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.defaults();
    }

}
