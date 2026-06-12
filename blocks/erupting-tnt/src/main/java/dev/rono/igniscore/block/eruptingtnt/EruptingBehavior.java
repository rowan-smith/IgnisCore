package dev.rono.igniscore.block.eruptingtnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.util.Vector;

final class EruptingBehavior {
    void onTick(RuntimeBlockInstance instance, BlockDefinition def) {
        int interval = StrategySupport.customInt(def, "eruptionInterval", 5);

        if (instance.getTicksLeft() % interval == 0 && instance.getTicksLeft() < StrategySupport.fuse(def, 100) - 10) {
            Location loc = Locations.toCenter(instance.getLocation());
            TNTPrimed tnt = loc.getWorld().spawn(loc, TNTPrimed.class);
            int eruptionFuse = StrategySupport.customInt(def, "eruptionFuse", 80);
            tnt.setFuseTicks(eruptionFuse);

            double horizontalPower = StrategySupport.customDouble(def, "eruptionHorizontalPower", 0.4);
            double verticalPower = StrategySupport.customDouble(def, "eruptionVerticalPower", 1.2);

            tnt.setVelocity(new Vector(
                    (Math.random() - 0.5) * horizontalPower,
                    verticalPower,
                    (Math.random() - 0.5) * horizontalPower
            ));

            loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.5f);
            loc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, loc, 1);
        }
    }

    void onTrigger(RuntimeBlockInstance instance) {
        Location loc = Locations.toCenter(instance.getLocation());
        StrategySupport.createExplosion(loc, instance.getDefinition(), 4.0, false);
    }
}
