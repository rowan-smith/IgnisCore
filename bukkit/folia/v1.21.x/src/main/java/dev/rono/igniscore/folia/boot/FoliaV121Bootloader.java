package dev.rono.igniscore.folia.boot;

import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.PlatformType;
import dev.rono.igniscore.folia.adapter.FoliaPlatformAdapter;
import dev.rono.igniscore.spigot.boot.JavaPluginBootloader;
import org.bukkit.plugin.java.JavaPlugin;

public final class FoliaV121Bootloader extends JavaPluginBootloader {

    public FoliaV121Bootloader() {
        super("folia-v1.21.x", PlatformType.FOLIA, "1.21.x", 150, 1, 21);
    }

    @Override
    protected PlatformAdapter createAdapter(JavaPlugin plugin) {
        return new FoliaPlatformAdapter(plugin);
    }
}
