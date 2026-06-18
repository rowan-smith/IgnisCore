package dev.rono.igniscore.spigot.boot;

import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.PlatformType;
import dev.rono.igniscore.spigot.adapter.BukkitPlatformAdapter;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpigotV261Bootloader extends JavaPluginBootloader {

    public SpigotV261Bootloader() {
        super("spigot-v26.1.x", PlatformType.SPIGOT, "26.1.x", 50, 26, 1);
    }

    @Override
    protected PlatformAdapter createAdapter(JavaPlugin plugin) {
        return new BukkitPlatformAdapter(plugin);
    }
}
