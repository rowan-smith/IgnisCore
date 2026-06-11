package dev.rono.igniscore.strategies;

import dev.rono.igniscore.api.IgnisCoreAPI;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.core.BuiltinStrategyBootstrap;
import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.RuntimeBlockInstance;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TunnelingStrategy extends BaseBlockBehaviorStrategy {
    public TunnelingStrategy() {
        super(IgnisStrategyDescriptor.of("tunneling", "Tunneling Blast", "1.0.0", "IgnisCore"));
    }

    @Override
    public dev.rono.igniscore.api.strategy.StrategyProfile profile(BlockDefinition definition) {
        return BuiltinStrategyBootstrap.explosiveProfile();
    }
    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        Location loc = instance.getLocation().toCenterLocation();
        Vector direction;
        if (context instanceof org.bukkit.event.block.TNTPrimeEvent event && event.getPrimingEntity() != null) {
            direction = event.getPrimingEntity().getLocation().getDirection();
        } else {
            direction = new Vector(0, 0, 1);
        }
        direction.setY(0).normalize();
        
        int tunnelLength = getCustomInt(instance.getDefinition(), "tunnelLength", 16);
        double tunnelGap = getCustomDouble(instance.getDefinition(), "tunnelGap", 2.0);

        new BukkitRunnable() {
            int count = 0;
            final Location current = loc.clone();
            @Override
            public void run() {
                if (count++ >= tunnelLength) {
                    cancel();
                    return;
                }
                current.add(direction.clone().multiply(tunnelGap));
                current.getWorld().createExplosion(current, 4.0f, false, true);
                current.getWorld().spawnParticle(Particle.SMOKE, current, 20, 0.5, 0.5, 0.5, 0.05);
            }
        }.runTaskTimer(IgnisCoreAPI.getBlockManager().getPlugin(), 0L, 4L);
    }
}
