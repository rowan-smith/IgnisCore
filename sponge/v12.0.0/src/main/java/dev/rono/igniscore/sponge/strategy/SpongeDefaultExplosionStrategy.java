package dev.rono.igniscore.sponge.strategy;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.api.strategy.StrategyProfiles;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.sponge.adapter.SpongeBridge;
import dev.rono.igniscore.sponge.support.SpongeRuntimeHolder;

public class SpongeDefaultExplosionStrategy extends AbstractIgnisBlockStrategy {
    public SpongeDefaultExplosionStrategy() {
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
        float power = StrategySupport.resolvePower(def, 4.0f);

        instance.getData().setDouble("ignis:blast_power", power);

        var defaultWorld = SpongeRuntimeHolder.server().worldManager().worlds().stream().findFirst().orElse(null);
        if (defaultWorld == null) {
            return;
        }
        IgnisWorld world = SpongeBridge.wrap(SpongeBridge.resolveWorld(loc, defaultWorld));
        world.playSound(loc, "entity.generic.explode", 1.0f, 1.0f);
        StrategySupport.createExplosion(world, loc, def, 4.0f, false);
    }
}
