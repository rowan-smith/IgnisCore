package dev.rono.igniscore.strategies;

import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.RuntimeBlockInstance;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.util.Vector;

public class MimicStrategy extends BaseBlockBehaviorStrategy {
    @Override
    public void onPlace(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        Location loc = instance.getLocation().toCenterLocation();
        
        int mimicCount = getCustomInt(def, "mimicCount", 4);
        double horizontalPower = getCustomDouble(def, "mimicHorizontalPower", 0.5);
        double verticalPower = getCustomDouble(def, "mimicVerticalPower", 0.2);

        for (int i = 0; i < mimicCount; i++) {
            TNTPrimed tnt = loc.getWorld().spawn(loc, TNTPrimed.class);
            tnt.setFuseTicks(instance.getTicksLeft());
            tnt.setVelocity(new Vector(
                    (Math.random() - 0.5) * horizontalPower,
                    verticalPower,
                    (Math.random() - 0.5) * horizontalPower
            ));
            tnt.setYield(0);
        }
        // Randomize fuse for unpredictability
        int fuse = def.getFuse();
        instance.setTicksLeft(fuse + (int) (Math.random() * 60 - 30));
    }

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        BlockDefinition def = instance.getDefinition();
        Location loc = instance.getLocation().toCenterLocation();
        float power = (float) getCustomDouble(def, "power", 4.0);
        loc.getWorld().createExplosion(loc, power, getCustomBoolean(def, "fire", false), getCustomBoolean(def, "blockDamage", true));
    }
}
