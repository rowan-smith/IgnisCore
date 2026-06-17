package dev.rono.igniscore.block.entitycamera;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new EntityCameraOnBlockClickListener());
        context.eventBus().subscribe(new EntityCameraOnBlockInteractListener(context));
    }

}
