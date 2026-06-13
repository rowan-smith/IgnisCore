package dev.rono.igniscore.paper.boot;

import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.PlatformBootloader;
import dev.rono.igniscore.api.port.PlatformType;
import dev.rono.igniscore.paper.adapter.PaperPlatformAdapter;
import dev.rono.igniscore.spigot.boot.BukkitBootloaderSupport;
import org.bukkit.plugin.java.JavaPlugin;

public final class PaperV121Bootloader implements PlatformBootloader {

    @Override
    public String id() {
        return "paper-v1.21.x";
    }

    @Override
    public PlatformType platformType() {
        return PlatformType.PAPER;
    }

    @Override
    public String minecraftVersionRange() {
        return "1.21.x";
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public boolean canBoot(Object host) {
        return BukkitBootloaderSupport.acceptsHost(host, PlatformType.PAPER, 1, 21);
    }

    @Override
    public PlatformAdapter boot(Object host) {
        return new PaperPlatformAdapter((JavaPlugin) host);
    }
}
