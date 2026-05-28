package dev.rono.igniscore.strategies;

import dev.rono.igniscore.model.RuntimeBlockInstance;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

public class WormholeStrategy extends BaseBlockBehaviorStrategy {
    @Override
    public void onTick(RuntimeBlockInstance instance) {
        Location loc = instance.getLocation().toCenterLocation();
        int ticksLeft = instance.getTicksLeft();
        double radius = 8.0 + (instance.getDefinition().getFuse() - ticksLeft) * 0.1;

        loc.getWorld().getNearbyEntities(loc, radius, radius, radius).forEach(entity -> {
            if (entity.getUniqueId().equals(instance.getDisplayEntity() != null ? instance.getDisplayEntity().getUniqueId() : null)) return;

            Vector pull = loc.toVector().subtract(entity.getLocation().toVector());
            double dist = pull.length();
            if (dist > 0.1) {
                pull.normalize().multiply(0.15 * (1.0 - dist / radius));
                entity.setVelocity(entity.getVelocity().add(pull));
            }
        });

        loc.getWorld().spawnParticle(Particle.PORTAL, loc, 15, 0.3, 0.3, 0.3, 0.2);
        if (ticksLeft % 5 == 0) {
            loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_AMBIENT, 1.0f, 0.5f + (float) (instance.getDefinition().getFuse() - ticksLeft) / 80.0f);
        }
    }

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        Location loc = instance.getLocation().toCenterLocation();
        loc.getWorld().createExplosion(loc, 10.0f, true, true);
        loc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, loc, 5, 2, 2, 2, 0);
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f);
    }
}
