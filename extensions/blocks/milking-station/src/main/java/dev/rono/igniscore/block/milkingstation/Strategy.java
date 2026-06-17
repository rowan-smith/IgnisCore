package dev.rono.igniscore.block.milkingstation;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.port.IgnisLocation;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final MilkingStationBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new MilkingStationBehavior(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder()
                .defaultFuse(80).combustible(false)
                .build();
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
