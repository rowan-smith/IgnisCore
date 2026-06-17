package dev.rono.igniscore.block.checkpointobelisk;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.CustomBlockAction;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new CheckpointObeliskListeners(context));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.defaults();
    }

}
