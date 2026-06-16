package dev.rono.igniscore.bootstrap;

import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.rono.igniscore.IgnisCoreApplication;
import dev.rono.igniscore.IgnisCoreModule;
import dev.rono.igniscore.api.IgnisCoreAPI;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.command.IgnisCommand;
import dev.rono.igniscore.command.PluginCommandHandler;
import dev.rono.igniscore.paper.command.PaperIgnisCommandHost;
import dev.rono.igniscore.paper.command.PaperIgnisCommandRegistrar;
import dev.rono.igniscore.spigot.boot.BukkitBootloaderSupport;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Unified plugin entrypoint that selects a version/platform bootloader at runtime.
 */
public final class IgnisBootstrapPlugin extends JavaPlugin implements PaperIgnisCommandHost {
    private Injector injector;
    private IgnisCoreApplication application;
    private PlatformAdapter platformAdapter;
    private PaperIgnisCommandRegistrar paperIgnisCommandRegistrar;

    @Override
    public void onEnable() {
        if (BukkitBootloaderSupport.isPaperRuntime()) {
            paperIgnisCommandRegistrar = new PaperIgnisCommandRegistrar();
            paperIgnisCommandRegistrar.install(this);
        }

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

        if (paperIgnisCommandRegistrar != null) {
            paperIgnisCommandRegistrar.bind(injector.getInstance(IgnisCommand.class));
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

    @Override
    public void bindPaperIgnisCommand(PluginCommandHandler command) {
        if (paperIgnisCommandRegistrar != null) {
            paperIgnisCommandRegistrar.bind(command);
        }
    }
}
