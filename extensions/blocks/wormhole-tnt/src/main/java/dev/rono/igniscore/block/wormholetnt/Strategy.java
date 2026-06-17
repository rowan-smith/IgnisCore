package dev.rono.igniscore.block.wormholetnt;

import dev.rono.igniscore.api.event.OnBlockTickListener;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        WormholeListeners listeners = new WormholeListeners(context);
        context.eventBus().subscribe((OnBlockTickListener) listeners);
        context.eventBus().subscribe((OnBlockTriggerListener) listeners);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder()
                .defaultFuse(100)
                .defaultRadius(12.0)
                .build();
    }

}
