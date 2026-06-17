package dev.rono.igniscore.block.socketlamp;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new SocketLampOnBlockClickListener());
        context.eventBus().subscribe(new SocketLampOnBlockPlaceListener(context));
        context.eventBus().subscribe(new SocketLampOnBlockBreakListener());
    }

}
