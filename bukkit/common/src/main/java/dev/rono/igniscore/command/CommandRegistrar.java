package dev.rono.igniscore.command;

import com.google.inject.Inject;
import dev.rono.igniscore.api.port.PlatformAdapter;

public class CommandRegistrar {
    private final PlatformAdapter platformAdapter;

    @Inject
    public CommandRegistrar(PlatformAdapter platformAdapter) {
        this.platformAdapter = platformAdapter;
    }

    public void register(String name, PluginCommandHandler handler) {
        platformAdapter.registerCommand(IgnisCommands.IGNIS, handler);
    }
}
