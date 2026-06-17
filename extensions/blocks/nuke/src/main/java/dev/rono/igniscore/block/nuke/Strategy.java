package dev.rono.igniscore.block.nuke;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        var listeners = new NukeListeners(context);
        onBlockPlace(listeners);
        onBlockActivate(listeners);
        onBlockTick(listeners);
        onBlockTrigger(listeners);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder()
                .defaultFuse(160)
                .defaultRadius(30.0)
                .build();
    }

}
