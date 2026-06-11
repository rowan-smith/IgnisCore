package dev.rono.blocks.mimic;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.util.Locations;
import org.bukkit.Location;
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
    public void onPlace(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        Location loc = Locations.toCenter(instance.getLocation());

        if (instance.getDisplayEntity() != null) {
            instance.getDisplayEntity().remove();
            instance.setDisplayEntity(null);
        }

        int mimicCount = getCustomInt(def, "mimicCount", 8);
        double horizontalPower = getCustomDouble(def, "mimicHorizontalPower", 1.0);
        double verticalPower = getCustomDouble(def, "mimicVerticalPower", 0.5);

        int totalCount = mimicCount + 1;
        int realIndex = (int) (Math.random() * totalCount);

        for (int i = 0; i < totalCount; i++) {
            TNTPrimed tnt = loc.getWorld().spawn(loc, TNTPrimed.class);

            int fuse = def.getFuse() + (int) (Math.random() * 40 - 20);
            tnt.setFuseTicks(Math.max(10, fuse));

            tnt.setVelocity(new Vector(
                    (Math.random() - 0.5) * horizontalPower,
                    verticalPower + (Math.random() * 0.4),
                    (Math.random() - 0.5) * horizontalPower
            ));

            if (i == realIndex) {
                tnt.setYield((float) getCustomDouble(def, "power", 4.0));
                tnt.setIsIncendiary(getCustomBoolean(def, "fire", false));
            } else {
                tnt.setYield(0);
            }
        }

        instance.setTicksLeft(0);
    }
}
