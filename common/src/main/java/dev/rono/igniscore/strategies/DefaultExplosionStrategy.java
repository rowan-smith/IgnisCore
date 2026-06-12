package dev.rono.igniscore.strategies;

import com.google.inject.Inject;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.api.strategy.StrategyProfiles;
import dev.rono.igniscore.api.strategy.StrategySupport;

public class DefaultExplosionStrategy extends AbstractIgnisBlockStrategy {
    private final ExtensionSupport extensionSupport;

    @Inject
    public DefaultExplosionStrategy(ExtensionSupport extensionSupport) {
        super(IgnisStrategyDescriptor.of("default", "Default Explosion", "1.0.0", "IgnisCore"));
        this.extensionSupport = extensionSupport;
    }

    @Override
    public dev.rono.igniscore.api.strategy.StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfiles.explosiveProfile();
    }

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = instance.getLocation();
        float power = StrategySupport.resolvePower(def, 4.0);

        instance.getData().setDouble("ignis:blast_power", power);

        IgnisWorld world = extensionSupport.resolveWorld(loc);
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.0f, 1.0f);
        StrategySupport.createExplosion(world, loc, def, 4.0, false);
    }
}
