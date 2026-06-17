package dev.rono.igniscore.block.riftgenerator;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new RiftGeneratorOnBlockClickListener());
        context.eventBus().subscribe(new RiftGeneratorOnBlockTickListener(context));
        context.eventBus().subscribe(new RiftGeneratorOnBlockTriggerListener(context));
    }

}
