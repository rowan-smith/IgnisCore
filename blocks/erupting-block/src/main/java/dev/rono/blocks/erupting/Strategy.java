package dev.rono.blocks.erupting;

import dev.rono.igniscore.api.strategy.AbstractIgnisStrategy;
import dev.rono.igniscore.api.strategy.ExplosiveStrategySupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.RuntimeBlockInstance;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.util.Vector;

public class Strategy extends AbstractIgnisStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(IgnisStrategyDescriptor.of("erupting", "Erupting TNT", "1.0.0", "IgnisCore", "erupting-block"),
                context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder().build();
    }

    @Override
    public void onTick(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        int interval = getCustomInt(def, "eruptionInterval", 5);

        if (instance.getTicksLeft() % interval == 0 && instance.getTicksLeft() < def.getFuse() - 10) {
            Location loc = instance.getLocation().toCenterLocation();
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
        Location loc = instance.getLocation().toCenterLocation();
        ExplosiveStrategySupport.createExplosion(loc, def, 4.0, false);
    }
}
