package dev.rono.igniscore.item.grenade;

import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.strategy.StrategySupport;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

final class GrenadeBehavior {
    private final Plugin plugin;

    GrenadeBehavior(Plugin plugin) {
        this.plugin = plugin;
    }

    void onItemUse(Player player, ItemDefinition definition, ItemStack item) {
        double velocity = StrategySupport.customDouble(definition.getCustomData(), "throw_velocity", 1.2);
        int fuseTicks = StrategySupport.customInt(definition.getCustomData(), "fuse_ticks", 40);

        Location spawn = player.getEyeLocation();
        Vector direction = spawn.getDirection().normalize().multiply(velocity);
        Snowball projectile = player.getWorld().spawn(spawn, Snowball.class, snowball -> {
            snowball.setShooter(player);
            snowball.setVelocity(direction);
        });

        item.setAmount(item.getAmount() - 1);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!projectile.isValid() || ticks++ >= fuseTicks) {
                    Location impact = projectile.isValid() ? projectile.getLocation() : spawn;
                    if (projectile.isValid()) {
                        projectile.remove();
                    }
                    impact.getWorld().playSound(impact, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                    StrategySupport.createExplosion(impact, definition, 4.0, false);
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }
}
