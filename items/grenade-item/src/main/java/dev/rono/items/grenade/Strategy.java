package dev.rono.items.grenade;

import dev.rono.igniscore.api.strategy.AbstractIgnisStrategy;
import dev.rono.igniscore.api.strategy.ExplosiveStrategySupport;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.model.ItemDefinition;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Strategy extends AbstractIgnisStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
    }

    @Override
    public void onItemUse(Player player, ItemDefinition definition, ItemStack item, Action action) {
        if (context == null) {
            return;
        }

        double velocity = ExplosiveStrategySupport.customDouble(definition.getCustomData(), "throw_velocity", 1.2);
        int fuseTicks = (int) ExplosiveStrategySupport.customDouble(definition.getCustomData(), "fuse_ticks", 40);

        Location spawn = player.getEyeLocation();
        Vector direction = spawn.getDirection().normalize().multiply(velocity);
        Snowball projectile = player.getWorld().spawn(spawn, Snowball.class, snowball -> {
            snowball.setShooter(player);
            snowball.setVelocity(direction);
        });

        consumeOne(item);

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
                    ExplosiveStrategySupport.createExplosion(impact, definition, 4.0, false);
                    cancel();
                }
            }
        }.runTaskTimer(context.getPlugin(), 1L, 1L);
    }

    private static void consumeOne(ItemStack item) {
        item.setAmount(item.getAmount() - 1);
    }
}
