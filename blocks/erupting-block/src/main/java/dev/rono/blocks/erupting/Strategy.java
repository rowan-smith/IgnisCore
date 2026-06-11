package dev.rono.blocks.erupting;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.util.Locations;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.util.Vector;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder().build();
    }

    @Override
    public void onTick(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        int interval = getCustomInt(def, "eruptionInterval", 5);

        if (instance.getTicksLeft() % interval == 0 && instance.getTicksLeft() < StrategySupport.fuse(def, 100) - 10) {
            Location loc = Locations.toCenter(instance.getLocation());
            TNTPrimed tnt = loc.getWorld().spawn(loc, TNTPrimed.class);
            int eruptionFuse = getCustomInt(def, "eruptionFuse", 80);
            tnt.setFuseTicks(eruptionFuse);

            double horizontalPower = getCustomDouble(def, "eruptionHorizontalPower", 0.4);
            double verticalPower = getCustomDouble(def, "eruptionVerticalPower", 1.2);

            tnt.setVelocity(new Vector(
                    (Math.random() - 0.5) * horizontalPower,
                    verticalPower,
                    (Math.random() - 0.5) * horizontalPower
            ));

            loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.5f);
            loc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, loc, 1);
        }
    }

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        BlockDefinition def = instance.getDefinition();
        Location loc = Locations.toCenter(instance.getLocation());
        StrategySupport.createExplosion(loc, def, 4.0, false);
    }
}
