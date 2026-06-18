package dev.rono.igniscore.spigot.boot;

import dev.rono.igniscore.api.port.PlatformType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class BukkitBootloaderSupport {
    private static final String PAPER_MARKER = "io.papermc.paper.datacomponent.DataComponentTypes";

    private BukkitBootloaderSupport() {
    }

    public static boolean isJavaPluginHost(Object host) {
        return host instanceof JavaPlugin;
    }

    public static boolean isPaperRuntime() {
        try {
            Class.forName(PAPER_MARKER);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    public static boolean isFoliaRuntime() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    public static String bukkitVersion() {
        return Bukkit.getBukkitVersion();
    }

    public static boolean matchesMinorLine(int major, int minor) {
        return dev.rono.igniscore.common.version.MinecraftVersions.matchesMinorLine(bukkitVersion(), major, minor);
    }

    public static boolean acceptsHost(Object host, PlatformType type, int major, int minor) {
        if (!isJavaPluginHost(host)) {
            return false;
        }
        if (!matchesMinorLine(major, minor)) {
            return false;
        }
        return switch (type) {
            case SPIGOT -> !isPaperRuntime() && !isFoliaRuntime();
            case PAPER -> isPaperRuntime() && !isFoliaRuntime();
            default -> false;
        };
    }
}
