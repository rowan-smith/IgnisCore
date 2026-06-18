package dev.rono.igniscore.paper;

import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.rono.igniscore.IgnisCoreApplication;
import dev.rono.igniscore.IgnisCoreModule;
import dev.rono.igniscore.api.IgnisCoreAPI;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.bootstrap.PlatformBootloaderLoader;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

/**
 * Dedicated Paper plugin entrypoint that boots via the Paper platform bootloaders.
 */
public final class IgnisPaperPlugin extends JavaPlugin {
    private static final String CONFIG_FILE = "paper-config.yml";

    private Injector injector;
    private IgnisCoreApplication application;
    private PlatformAdapter platformAdapter;
    private FileConfiguration config;

    @Override
    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    @Override
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(getConfigFile());
        try (InputStream defaultStream = getResource(CONFIG_FILE)) {
            if (defaultStream != null) {
                config.setDefaults(YamlConfiguration.loadConfiguration(
                        new InputStreamReader(defaultStream, StandardCharsets.UTF_8)));
                config.options().copyDefaults(true);
            }
        } catch (IOException error) {
            getLogger().log(Level.SEVERE, "Could not load default " + CONFIG_FILE, error);
        }
    }

    @Override
    public void saveDefaultConfig() {
        if (!getConfigFile().exists()) {
            saveResource(CONFIG_FILE, false);
        }
    }

    @Override
    protected File getConfigFile() {
        return new File(getDataFolder(), CONFIG_FILE);
    }

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
