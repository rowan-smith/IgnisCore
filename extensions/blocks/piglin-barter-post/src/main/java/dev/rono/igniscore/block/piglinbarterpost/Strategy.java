package dev.rono.igniscore.block.piglinbarterpost;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.extensions.shared.strategy.PlacedClickListener;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(PlacedClickListener.fixed(CustomBlockAction.BREAK, CustomBlockAction.OPEN));
        PiglinBarterPostRuntime runtime = new PiglinBarterPostRuntime(context);
        context.eventBus().subscribe(new PiglinBarterPostOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new PiglinBarterPostOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new PiglinBarterPostOnBlockInteractListener(runtime));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.placed();
    }

}
