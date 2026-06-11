package dev.rono.blocks.tunneling;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.util.Locations;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;
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
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        Location loc = Locations.toCenter(instance.getLocation());
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
        }.runTaskTimer(Strategy.this.context.getPlugin(), 0L, 4L);
    }
}
