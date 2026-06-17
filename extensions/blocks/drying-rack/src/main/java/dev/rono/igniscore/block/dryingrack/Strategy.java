package dev.rono.igniscore.block.dryingrack;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.port.IgnisLocation;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final DryingRackBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new DryingRackBehavior(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.defaults();
    }

    @Override
    public void onPlaced(BlockDefinition definition, IgnisLocation location) {
        behavior.onPlaced(definition, location);
    }
    @Override
    public void onPlacedBreak(BlockDefinition definition, IgnisLocation location) {
        behavior.onPlacedBreak(definition, location);
    }

}
