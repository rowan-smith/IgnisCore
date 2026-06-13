package dev.rono.igniscore.sponge.v1900;

import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.rono.igniscore.api.IgnisCoreAPI;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.bootstrap.PlatformBootloaderLoader;
import dev.rono.igniscore.core.IgnisCoreFacadeImpl;
import dev.rono.igniscore.sponge.SpongeIgnisApplication;
import dev.rono.igniscore.sponge.SpongeIgnisModule;
import dev.rono.igniscore.sponge.SpongePluginHost;
import dev.rono.igniscore.sponge.command.SpongeIgnisCommand;
import dev.rono.igniscore.sponge.support.SpongeRuntimeHolder;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import com.google.inject.Inject;

@Plugin("igniscore")
public final class IgnisSpongePlugin implements SpongePluginHost {
    private final PluginContainer container;
    private final Game game;
    private final Logger logger;

    private Injector injector;
    private SpongeIgnisApplication application;
    private PlatformAdapter platformAdapter;

    @Inject
    public IgnisSpongePlugin(PluginContainer container, Game game) {
        this.container = container;
        this.game = game;
        this.logger = container.logger();
    }

    @Listener
    public void onRegisterCommands(RegisterCommandEvent<org.spongepowered.api.command.Command.Parameterized> event) {
        if (injector == null) {
            return;
        }
        event.register(container, injector.getInstance(SpongeIgnisCommand.class).build(), "ignis");
    }

    @Listener
    public void onStarted(StartedEngineEvent<Server> event) {
        SpongeRuntimeHolder.bind(event.engine());
        platformAdapter = PlatformBootloaderLoader.boot(this);
        injector = Guice.createInjector(new SpongeIgnisModule(this, platformAdapter));
        application = injector.getInstance(SpongeIgnisApplication.class);

        IgnisCoreAPI.init(injector.getInstance(IgnisCoreFacadeImpl.class));
        application.enable();

        logger.info("IgnisCore enabled on Sponge for Minecraft " + platformAdapter.getMinecraftVersion());
    }

    @Listener
    public void onStopping(StoppingEngineEvent<Server> event) {
        if (application != null) {
            application.disable();
        }
        if (platformAdapter != null) {
            platformAdapter.shutdown();
        }
        SpongeRuntimeHolder.clear();
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public PlatformAdapter platformAdapter() {
        return platformAdapter;
    }

    @Override
    public PluginContainer container() {
        return container;
    }

    @Override
    public Game game() {
        return game;
    }
}
