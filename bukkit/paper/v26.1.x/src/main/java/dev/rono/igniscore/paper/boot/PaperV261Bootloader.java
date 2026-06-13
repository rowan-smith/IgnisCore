package dev.rono.igniscore.paper.boot;

import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.PlatformType;
import dev.rono.igniscore.paper.adapter.PaperPlatformAdapter;
import dev.rono.igniscore.spigot.boot.JavaPluginBootloader;
import org.bukkit.plugin.java.JavaPlugin;

public final class PaperV261Bootloader extends JavaPluginBootloader {

    public PaperV261Bootloader() {
        super("paper-v26.1.x", PlatformType.PAPER, "26.1.x", 100, 26, 1);
    }

    @Override
    protected PlatformAdapter createAdapter(JavaPlugin plugin) {
        return new PaperPlatformAdapter(plugin);
    }
}
