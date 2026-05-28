package dev.rono.igniscore.strategies;

import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.RuntimeBlockInstance;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.util.Vector;

public class EruptingStrategy extends BaseBlockBehaviorStrategy {
    @Override
    public void onTick(RuntimeBlockInstance instance) {
        if (instance.getTicksLeft() % 15 == 0 && instance.getTicksLeft() < instance.getDefinition().getFuse() - 10) {
            Location loc = instance.getLocation().toCenterLocation();
            TNTPrimed tnt = loc.getWorld().spawn(loc, TNTPrimed.class);
            tnt.setFuseTicks(40);
            tnt.setVelocity(new Vector(Math.random() - 0.5, 0.8, Math.random() - 0.5).multiply(0.6));
            loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.5f);
            loc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, loc, 1);
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
