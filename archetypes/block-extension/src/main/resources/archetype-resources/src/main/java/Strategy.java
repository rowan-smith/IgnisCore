package ${package};

import dev.rono.igniscore.api.config.ExtensionConfigs;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.strategy.StrategySupport;

public class Strategy extends AbstractIgnisBlockStrategy {
    public Strategy(IgnisStrategyContext context) {
        super(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        var explosion = ExtensionConfigs.explosion(definition);
        return StrategyProfile.builder()
                .defaultFuse(explosion.fuse())
                .defaultRadius(explosion.radius())
                .build();
    }

    @Override
    public void onPlaced(BlockDefinition definition, IgnisLocation location) {
        var world = context.getExtensionSupport().resolveWorld(location);
        world.spawnParticle(location, "SMOKE", 6, 0.2, 0.2, 0.2, 0.01);
    }

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        BlockDefinition definition = instance.getDefinition();
        var explosion = ExtensionConfigs.explosion(definition);
        IgnisLocation location = instance.getLocation();
        var world = context.getExtensionSupport().resolveWorld(location);
        StrategySupport.createExplosion(world, location, definition, explosion.power(), explosion.fire());
    }
}
