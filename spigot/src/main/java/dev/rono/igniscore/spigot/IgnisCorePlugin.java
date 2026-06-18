package dev.rono.igniscore.spigot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.rono.igniscore.IgnisCoreApplication;
import dev.rono.igniscore.IgnisCoreModule;
import dev.rono.igniscore.api.IgnisCoreAPI;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.bootstrap.PlatformBootloaderLoader;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Spigot plugin entrypoint that selects a version/platform bootloader at runtime.
 */
public final class IgnisCorePlugin extends JavaPlugin {
    private Injector injector;
    private IgnisCoreApplication application;
    private PlatformAdapter platformAdapter;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        platformAdapter = PlatformBootloaderLoader.boot(this);
        getLogger().info("IgnisCore booted via " + platformAdapter.getPlatformType()
                + " adapter for Minecraft " + platformAdapter.getMinecraftVersion());

        try {
            injector = Guice.createInjector(new IgnisCoreModule(this, platformAdapter));
            application = injector.getInstance(IgnisCoreApplication.class);
        } catch (RuntimeException error) {
            getLogger().severe("Failed to initialize IgnisCore services: " + error.getClass().getSimpleName()
                    + " - " + error.getMessage());
            error.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        IgnisCoreAPI.init(application);
        try {
            application.enable();
        } catch (RuntimeException error) {
            getLogger().severe("Failed to enable IgnisCore: " + error.getClass().getSimpleName()
                    + " - " + error.getMessage());
            error.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (application != null) {
            application.disable();
        }
        if (platformAdapter != null) {
            platformAdapter.shutdown();
        }
    }
}
