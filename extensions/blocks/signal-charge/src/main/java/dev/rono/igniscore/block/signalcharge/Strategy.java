package dev.rono.igniscore.block.signalcharge;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final SignalChargeBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new SignalChargeBehavior(context);
    }

    @Override
    public void registerEvents() {
        onBlockTrigger(event -> behavior.onTrigger(event.instance()));
    }
}
