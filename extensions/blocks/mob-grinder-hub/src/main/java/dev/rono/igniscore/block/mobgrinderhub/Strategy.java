package dev.rono.igniscore.block.mobgrinderhub;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.extensions.shared.strategy.PlacedClickListener;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(PlacedClickListener.fixed(CustomBlockAction.BREAK, CustomBlockAction.NONE));
        context.eventBus().subscribe(new MobGrinderHubOnBlockPlaceListener(context));
        context.eventBus().subscribe(new MobGrinderHubOnBlockBreakListener());
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.placed();
    }

}
