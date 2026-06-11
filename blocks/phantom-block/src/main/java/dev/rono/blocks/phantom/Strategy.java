package dev.rono.blocks.phantom;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

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
        if (instance.getTicksLeft() == StrategySupport.fuse(instance.getDefinition(), 160) - 20) {
            if (instance.getDisplayEntity() != null) {
                instance.getDisplayEntity().remove();
                instance.setDisplayEntity(null);
            }
            Location loc = instance.getLocation().toCenterLocation();
            loc.getWorld().spawnParticle(Particle.SPORE_BLOSSOM_AIR, loc, 20, 0.5, 0.5, 0.5, 0.05);
            loc.getWorld().playSound(loc, Sound.ENTITY_PHANTOM_AMBIENT, 1.0f, 0.5f);
        }
    }

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        BlockDefinition def = instance.getDefinition();
        Location loc = instance.getLocation().toCenterLocation();
        StrategySupport.createExplosion(loc, def, 4.0, false);
    }
}
