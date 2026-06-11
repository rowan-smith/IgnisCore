package dev.rono.igniscore.strategies;

import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.core.BuiltinStrategyBootstrap;
import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.RuntimeBlockInstance;
import org.bukkit.Location;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.util.Vector;

public class MimicStrategy extends BaseBlockBehaviorStrategy {
    public MimicStrategy() {
        super(IgnisStrategyDescriptor.of("mimic", "Mimic TNT", "1.0.0", "IgnisCore"));
    }

    @Override
    public dev.rono.igniscore.api.strategy.StrategyProfile profile(BlockDefinition definition) {
        return BuiltinStrategyBootstrap.explosiveProfile();
    }
    @Override
    public void onPlace(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        Location loc = instance.getLocation().toCenterLocation();
        
        // Remove display entity immediately so the block disappears on ignition
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
            
            // Randomize fuse slightly for more unpredictability
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

        // Trigger immediate removal of the runtime instance as the behavior is now handled by entities
        instance.setTicksLeft(0);
    }
}
