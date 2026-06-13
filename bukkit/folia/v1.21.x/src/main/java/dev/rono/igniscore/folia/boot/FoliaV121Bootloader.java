package dev.rono.igniscore.folia.boot;

import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.PlatformBootloader;
import dev.rono.igniscore.api.port.PlatformType;
import dev.rono.igniscore.folia.adapter.FoliaPlatformAdapter;
import dev.rono.igniscore.spigot.boot.BukkitBootloaderSupport;
import org.bukkit.plugin.java.JavaPlugin;

public final class FoliaV121Bootloader implements PlatformBootloader {

    @Override
    public String id() {
        return "folia-v1.21.x";
    }

    @Override
    public PlatformType platformType() {
        return PlatformType.FOLIA;
    }

    @Override
    public String minecraftVersionRange() {
        return "1.21.x";
    }

    @Override
    public int priority() {
        return 150;
    }

    @Override
    public boolean canBoot(Object host) {
        return BukkitBootloaderSupport.acceptsHost(host, PlatformType.FOLIA, 1, 21);
    }

    @Override
    public PlatformAdapter boot(Object host) {
        return new FoliaPlatformAdapter((JavaPlugin) host);
    }
}
