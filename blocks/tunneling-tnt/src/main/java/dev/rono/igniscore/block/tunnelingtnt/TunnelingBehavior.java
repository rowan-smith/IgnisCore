package dev.rono.igniscore.block.tunnelingtnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

final class TunnelingBehavior {
    private final Plugin plugin;

    TunnelingBehavior(Plugin plugin) {
        this.plugin = plugin;
    }

    void onTrigger(RuntimeBlockInstance instance, Object context) {
        Location loc = Locations.toCenter(instance.getLocation());
        Vector direction;
        if (context instanceof org.bukkit.event.block.TNTPrimeEvent event && event.getPrimingEntity() != null) {
            direction = event.getPrimingEntity().getLocation().getDirection();
        } else {
            direction = new Vector(0, 0, 1);
        }
        direction.setY(0).normalize();

        BlockDefinition def = instance.getDefinition();
        int tunnelLength = StrategySupport.customInt(def, "tunnelLength", 16);
        double tunnelGap = StrategySupport.customDouble(def, "tunnelGap", 2.0);

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
        }.runTaskTimer(plugin, 0L, 4L);
    }
}
