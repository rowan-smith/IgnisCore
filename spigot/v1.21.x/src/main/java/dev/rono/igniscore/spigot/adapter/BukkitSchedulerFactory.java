package dev.rono.igniscore.spigot.adapter;

import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.spigot.support.FoliaSupport;
import org.bukkit.plugin.java.JavaPlugin;

public final class BukkitSchedulerFactory {
    private BukkitSchedulerFactory() {
    }

    public static IgnisScheduler create(JavaPlugin plugin) {
        if (FoliaSupport.isFolia()) {
            return new FoliaIgnisScheduler(plugin);
        }
        return new BukkitIgnisScheduler(plugin);
    }
}
