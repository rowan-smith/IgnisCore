package dev.rono.blocks.signalcharge;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import org.bukkit.Location;
import org.bukkit.Sound;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder()
                .combustible(false)
                .leftClickAction(CustomBlockAction.NONE)
                .rightClickAction(CustomBlockAction.NONE)
                .placementSound("BLOCK_TNT_PLACE")
                .build();
    }

    @Override
    public void onPlace(RuntimeBlockInstance instance) {
        instance.setTicksLeft(0);
    }

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        BlockDefinition def = instance.getDefinition();
        Location loc = instance.getLocation().toCenterLocation();
        loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.9f);
        StrategySupport.createExplosion(loc, def, 4.0, false);
    }
}
