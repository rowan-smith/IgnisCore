package dev.rono.igniscore.block.nuke;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import org.bukkit.Location;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final NukeBehavior behavior = new NukeBehavior();

    public Strategy(IgnisStrategyContext context) {
        super(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder()
                .defaultFuse(160)
                .defaultRadius(30.0)
                .placementSound("BLOCK_BEACON_ACTIVATE")
                .build();
    }

    @Override
    public void onStaticPlace(BlockDefinition definition, Location location) {
        behavior.onStaticPlace(location);
    }

    @Override
    public void onPlace(RuntimeBlockInstance instance) {
        behavior.onPlace(instance);
    }

    @Override
    public void onTick(RuntimeBlockInstance instance) {
        behavior.onTick(instance);
    }

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        behavior.onTrigger(instance, instance.getDefinition());
    }
}
