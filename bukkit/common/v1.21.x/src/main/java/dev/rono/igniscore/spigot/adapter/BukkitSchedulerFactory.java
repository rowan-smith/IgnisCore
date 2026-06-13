package dev.rono.igniscore.spigot.adapter;

import dev.rono.igniscore.api.port.IgnisScheduler;
import org.bukkit.plugin.java.JavaPlugin;

public final class BukkitSchedulerFactory {
    private static final String FOLIA_MARKER = "io.papermc.paper.threadedregions.RegionizedServer";
    private static final String FOLIA_SCHEDULER = "dev.rono.igniscore.folia.adapter.FoliaIgnisScheduler";

    private BukkitSchedulerFactory() {
    }

    public static IgnisScheduler create(JavaPlugin plugin) {
        if (isFoliaRuntime()) {
            return createFoliaScheduler(plugin);
        }
        return new BukkitIgnisScheduler(plugin);
    }

    private static boolean isFoliaRuntime() {
        try {
            Class.forName(FOLIA_MARKER);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private static IgnisScheduler createFoliaScheduler(JavaPlugin plugin) {
        try {
            Class<?> schedulerType = Class.forName(FOLIA_SCHEDULER);
            return (IgnisScheduler) schedulerType.getConstructor(JavaPlugin.class).newInstance(plugin);
        } catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Folia runtime detected but " + FOLIA_SCHEDULER + " is unavailable", error);
        }
    }
}
