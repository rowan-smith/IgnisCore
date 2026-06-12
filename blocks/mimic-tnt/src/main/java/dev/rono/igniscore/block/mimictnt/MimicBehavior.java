package dev.rono.igniscore.block.mimictnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;
import org.bukkit.Location;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.util.Vector;

final class MimicBehavior {
    void onPlace(RuntimeBlockInstance instance, BlockDefinition def) {
        Location loc = Locations.toCenter(instance.getLocation());

        if (instance.getDisplayEntity() != null) {
            instance.getDisplayEntity().remove();
            instance.setDisplayEntity(null);
        }

        int mimicCount = StrategySupport.customInt(def, "mimicCount", 8);
        double horizontalPower = StrategySupport.customDouble(def, "mimicHorizontalPower", 1.0);
        double verticalPower = StrategySupport.customDouble(def, "mimicVerticalPower", 0.5);

        int totalCount = mimicCount + 1;
        int realIndex = (int) (Math.random() * totalCount);

        for (int i = 0; i < totalCount; i++) {
            TNTPrimed tnt = loc.getWorld().spawn(loc, TNTPrimed.class);

            int fuse = StrategySupport.fuse(def, 80) + (int) (Math.random() * 40 - 20);
            tnt.setFuseTicks(Math.max(10, fuse));

            tnt.setVelocity(new Vector(
                    (Math.random() - 0.5) * horizontalPower,
                    verticalPower + (Math.random() * 0.4),
                    (Math.random() - 0.5) * horizontalPower
            ));

            if (i == realIndex) {
                tnt.setYield((float) StrategySupport.customDouble(def, "power", 4.0));
                tnt.setIsIncendiary(StrategySupport.customBoolean(def, "fire", false));
            } else {
                tnt.setYield(0);
            }
        }

        instance.setTicksLeft(0);
    }
}
