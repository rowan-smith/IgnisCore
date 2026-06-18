package dev.rono.igniscore.spigot.adapter;

import dev.rono.igniscore.api.port.IgnisScheduler;
import org.bukkit.plugin.java.JavaPlugin;

public final class BukkitSchedulerFactory {
    private BukkitSchedulerFactory() {
    }

    public static IgnisScheduler create(JavaPlugin plugin) {
        return new BukkitIgnisScheduler(plugin);
    }
}
