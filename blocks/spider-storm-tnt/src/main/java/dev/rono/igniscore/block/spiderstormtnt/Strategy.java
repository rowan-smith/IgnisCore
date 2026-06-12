package dev.rono.igniscore.block.spiderstormtnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import org.bukkit.Location;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final SpiderStormBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new SpiderStormBehavior(context.getNbtService());
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder()
                .placementSound("ENTITY_SPIDER_AMBIENT")
                .build();
    }

    @Override
    public void onStaticPlace(BlockDefinition definition, Location location) {
        behavior.onStaticPlace(location);
    }

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        behavior.onTrigger(instance);
    }
}
