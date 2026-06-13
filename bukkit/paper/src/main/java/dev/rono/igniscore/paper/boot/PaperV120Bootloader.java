package dev.rono.igniscore.paper.boot;

import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.PlatformType;
import dev.rono.igniscore.paper.adapter.PaperPlatformAdapter;
import dev.rono.igniscore.spigot.boot.JavaPluginBootloader;
import org.bukkit.plugin.java.JavaPlugin;

public final class PaperV120Bootloader extends JavaPluginBootloader {

    public PaperV120Bootloader() {
        super("paper-v1.20.x", PlatformType.PAPER, "1.20.x", 100, 1, 20);
    }

    @Override
    protected PlatformAdapter createAdapter(JavaPlugin plugin) {
        return new PaperPlatformAdapter(plugin);
    }
}
