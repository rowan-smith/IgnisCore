package dev.rono.igniscore.block.spiderstormtnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.extensions.shared.strategy.StrategyProfiles;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final SpiderStormBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new SpiderStormBehavior(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfiles.explosiveProfile().toBuilder()
                .defaultFuse(80)
                .placementSound("ENTITY_SPIDER_AMBIENT")
                .build();
    }

    @Override
    public void onPlaced(BlockDefinition definition, IgnisLocation location) {
        behavior.onPlaced(location);
    }

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        behavior.onTrigger(instance);
    }
}
