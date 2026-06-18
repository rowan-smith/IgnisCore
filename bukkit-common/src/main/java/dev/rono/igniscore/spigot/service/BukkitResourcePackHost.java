package dev.rono.igniscore.spigot.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.port.ResourcePackHost;
import dev.rono.igniscore.resourcepack.ResourcePackService;

public class BukkitResourcePackHost implements ResourcePackHost {
    private final ResourcePackService resourcePackService;

    @Inject
    public BukkitResourcePackHost(ResourcePackService resourcePackService) {
        this.resourcePackService = resourcePackService;
    }

    @Override
    public void buildAndRegister() throws java.io.IOException {
        resourcePackService.buildAndRegister();
    }

    @Override
    public void startServer() {
        resourcePackService.startServer();
    }

    @Override
    public void stopServer() {
        resourcePackService.stopServer();
    }

    @Override
    public void reloadConfiguration() {
        resourcePackService.reloadConfiguration();
    }

    @Override
    public void buildAndRegisterAsync(Runnable onSuccess, java.util.function.Consumer<java.io.IOException> onFailure) {
        resourcePackService.buildAndRegisterAsync(onSuccess, onFailure);
    }
}
