package dev.rono.igniscore.spigot.boot;

import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.PlatformBootloader;
import dev.rono.igniscore.api.port.PlatformType;
import dev.rono.igniscore.spigot.adapter.BukkitPlatformAdapter;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpigotV121Bootloader implements PlatformBootloader {

    @Override
    public String id() {
        return "spigot-v1.21.x";
    }

    @Override
    public PlatformType platformType() {
        return PlatformType.SPIGOT;
    }

    @Override
    public String minecraftVersionRange() {
        return "1.21.x";
    }

    @Override
    public int priority() {
        return 50;
    }

    @Override
    public boolean canBoot(Object host) {
        return BukkitBootloaderSupport.acceptsHost(host, PlatformType.SPIGOT, 1, 21);
    }

    @Override
    public PlatformAdapter boot(Object host) {
        return new BukkitPlatformAdapter((JavaPlugin) host);
    }
}
