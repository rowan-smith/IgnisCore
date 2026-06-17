package dev.rono.igniscore.block.picnicbasket;

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
        PicnicBasketRuntime runtime = new PicnicBasketRuntime(context);
        context.eventBus().subscribe(new PicnicBasketOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new PicnicBasketOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new PicnicBasketOnBlockInteractListener(runtime));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.placed();
    }

}
