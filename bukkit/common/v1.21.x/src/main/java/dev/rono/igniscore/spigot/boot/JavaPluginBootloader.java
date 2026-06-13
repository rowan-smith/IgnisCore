package dev.rono.igniscore.spigot.boot;

import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.port.PlatformBootloader;
import dev.rono.igniscore.api.port.PlatformType;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Shared {@link PlatformBootloader} implementation for Bukkit-family hosts.
 */
public abstract class JavaPluginBootloader implements PlatformBootloader {
    private final String id;
    private final PlatformType platformType;
    private final String minecraftVersionRange;
    private final int priority;
    private final int majorVersion;
    private final int minorVersion;

    protected JavaPluginBootloader(String id,
                                   PlatformType platformType,
                                   String minecraftVersionRange,
                                   int priority,
                                   int majorVersion,
                                   int minorVersion) {
        this.id = id;
        this.platformType = platformType;
        this.minecraftVersionRange = minecraftVersionRange;
        this.priority = priority;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    protected abstract PlatformAdapter createAdapter(JavaPlugin plugin);

    @Override
    public final String id() {
        return id;
    }

    @Override
    public final PlatformType platformType() {
        return platformType;
    }

    @Override
    public final String minecraftVersionRange() {
        return minecraftVersionRange;
    }

    @Override
    public final int priority() {
        return priority;
    }

    @Override
    public final boolean canBoot(Object host) {
        return BukkitBootloaderSupport.acceptsHost(host, platformType, majorVersion, minorVersion);
    }

    @Override
    public final PlatformAdapter boot(Object host) {
        if (!(host instanceof JavaPlugin plugin)) {
            throw new IllegalArgumentException("Bootloader " + id + " requires a JavaPlugin host");
        }
        return createAdapter(plugin);
    }
}
