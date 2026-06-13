package dev.rono.igniscore.spigot.boot;

import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.PlatformType;
import dev.rono.igniscore.spigot.adapter.BukkitPlatformAdapter;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpigotV121Bootloader extends JavaPluginBootloader {

    public SpigotV121Bootloader() {
        super("spigot-v1.21.x", PlatformType.SPIGOT, "1.21.x", 50, 1, 21);
    }

    @Override
    protected PlatformAdapter createAdapter(JavaPlugin plugin) {
        return new BukkitPlatformAdapter(plugin);
    }
}
