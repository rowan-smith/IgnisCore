package dev.rono.igniscore.block.nuke;

import dev.rono.igniscore.api.event.OnBlockActivateListener;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.event.OnBlockTickListener;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        NukeListeners listeners = new NukeListeners(context);
        context.eventBus().subscribe((OnBlockPlaceListener) listeners);
        context.eventBus().subscribe((OnBlockActivateListener) listeners);
        context.eventBus().subscribe((OnBlockTickListener) listeners);
        context.eventBus().subscribe((OnBlockTriggerListener) listeners);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder()
                .defaultFuse(160)
                .defaultRadius(30.0)
                .build();
    }

}
