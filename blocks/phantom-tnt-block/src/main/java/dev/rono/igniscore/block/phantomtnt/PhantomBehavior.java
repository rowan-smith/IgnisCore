package dev.rono.igniscore.block.phantomtnt;

import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

final class PhantomBehavior {
    void onTick(RuntimeBlockInstance instance) {
        if (instance.getTicksLeft() == StrategySupport.fuse(instance.getDefinition(), 160) - 20) {
            if (instance.getDisplayEntity() != null) {
                instance.getDisplayEntity().remove();
                instance.setDisplayEntity(null);
            }
            Location loc = Locations.toCenter(instance.getLocation());
            loc.getWorld().spawnParticle(Particle.SPORE_BLOSSOM_AIR, loc, 20, 0.5, 0.5, 0.5, 0.05);
            loc.getWorld().playSound(loc, Sound.ENTITY_PHANTOM_AMBIENT, 1.0f, 0.5f);
        }
    }

    void onTrigger(RuntimeBlockInstance instance) {
        Location loc = Locations.toCenter(instance.getLocation());
        StrategySupport.createExplosion(loc, instance.getDefinition(), 4.0, false);
    }
}
