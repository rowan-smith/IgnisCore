package dev.rono.igniscore.strategies;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.api.strategy.StrategyProfiles;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;

public class DefaultExplosionStrategy extends AbstractIgnisBlockStrategy {
    public DefaultExplosionStrategy() {
        super(IgnisStrategyDescriptor.of("default", "Default Explosion", "1.0.0", "IgnisCore"));
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

        IgnisWorld world = BukkitBridge.wrap(BukkitBridge.toBukkit(loc).getWorld());
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.0f, 1.0f);
        StrategySupport.createExplosion(world, loc, def, 4.0, false);
    }
}
