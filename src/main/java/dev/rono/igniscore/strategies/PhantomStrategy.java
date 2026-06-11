package dev.rono.igniscore.strategies;

import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.core.BuiltinStrategyBootstrap;
import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.RuntimeBlockInstance;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class PhantomStrategy extends BaseBlockBehaviorStrategy {
    public PhantomStrategy() {
        super(IgnisStrategyDescriptor.of("phantom", "Phantom Vanish", "1.0.0", "IgnisCore"));
    }

    @Override
    public dev.rono.igniscore.api.strategy.StrategyProfile profile(BlockDefinition definition) {
        return BuiltinStrategyBootstrap.explosiveProfile();
    }
    @Override
    public void onTick(RuntimeBlockInstance instance) {
        if (instance.getTicksLeft() == instance.getDefinition().getFuse() - 20) {
            if (instance.getDisplayEntity() != null) {
                instance.getDisplayEntity().remove();
                instance.setDisplayEntity(null);
            }
            Location loc = instance.getLocation().toCenterLocation();
            loc.getWorld().spawnParticle(Particle.SPORE_BLOSSOM_AIR, loc, 20, 0.5, 0.5, 0.5, 0.05);
            loc.getWorld().playSound(loc, Sound.ENTITY_PHANTOM_AMBIENT, 1.0f, 0.5f);
        }
    }

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        BlockDefinition def = instance.getDefinition();
        Location loc = instance.getLocation().toCenterLocation();
        float power = (float) getCustomDouble(def, "power", 4.0);
        loc.getWorld().createExplosion(loc, power, getCustomBoolean(def, "fire", false), getCustomBoolean(def, "blockDamage", true));
    }
}
