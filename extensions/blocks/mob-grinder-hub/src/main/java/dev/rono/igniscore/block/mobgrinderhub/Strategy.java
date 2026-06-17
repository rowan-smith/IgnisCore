package dev.rono.igniscore.block.mobgrinderhub;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final MobGrinderHubBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new MobGrinderHubBehavior(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.defaults();
    }

    @Override
    public void registerEvents() {
        onBlockPlace(event -> behavior.onPlaced(event.definition(), event.location()));
        onBlockBreak(event -> behavior.onPlacedBreak(event.definition(), event.location()));
    }
}
