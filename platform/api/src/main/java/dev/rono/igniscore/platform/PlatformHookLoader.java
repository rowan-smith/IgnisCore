package dev.rono.igniscore.platform;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class PlatformHookLoader {
    private static final String PAPER_HOOKS_CLASS = "dev.rono.igniscore.platform.paper.PaperPlatformHooks";
    private static final String SPIGOT_HOOKS_CLASS = "dev.rono.igniscore.platform.spigot.SpigotPlatformHooks";
    private static final String PAPER_MARKER_CLASS = "io.papermc.paper.datacomponent.DataComponentTypes";

    private PlatformHookLoader() {
    }

    public static PlatformHooks load(JavaPlugin plugin) {
        if (isPaperRuntime()) {
            PlatformHooks paperHooks = instantiate(PAPER_HOOKS_CLASS, plugin);
            if (paperHooks != null) {
                plugin.getLogger().info("Using Paper platform hooks.");
                return paperHooks;
            }
            plugin.getLogger().warning("Paper runtime detected but Paper hooks could not be loaded; falling back to Spigot hooks.");
        }

        PlatformHooks spigotHooks = instantiate(SPIGOT_HOOKS_CLASS, plugin);
        if (spigotHooks != null) {
            plugin.getLogger().info("Using Spigot platform hooks (Adventure via BukkitAudiences).");
            return spigotHooks;
        }

        throw new IllegalStateException("No platform hooks implementation found on the classpath.");
    }

    private static boolean isPaperRuntime() {
        try {
            Class.forName(PAPER_MARKER_CLASS);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

    private static PlatformHooks instantiate(String className, JavaPlugin plugin) {
        try {
            Class<?> hookClass = Class.forName(className);
            if (SPIGOT_HOOKS_CLASS.equals(className)) {
                Object instance = hookClass.getConstructor(JavaPlugin.class).newInstance(plugin);
                return (PlatformHooks) instance;
            }
            Object instance = hookClass.getConstructor().newInstance();
            return (PlatformHooks) instance;
        } catch (ReflectiveOperationException | ClassCastException ignored) {
            return null;
        }
    }

    public static void logLoadFailure(JavaPlugin plugin, String className, Throwable error) {
        plugin.getLogger().log(Level.WARNING, "Failed to load platform hooks from " + className, error);
    }
}
