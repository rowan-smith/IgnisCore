package dev.rono.blocks.phantom;

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

public class Strategy extends AbstractIgnisStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(IgnisStrategyDescriptor.of("phantom", "Phantom Vanish", "1.0.0", "IgnisCore", "phantom-block"),
                context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder().build();
    }

    @Override
    public void onTick(RuntimeBlockInstance instance) {
        if (instance.getTicksLeft() == instance.getDefinition().getFuse() - 20) {
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
        ExplosiveStrategySupport.createExplosion(loc, def, 4.0, false);
    }
}
