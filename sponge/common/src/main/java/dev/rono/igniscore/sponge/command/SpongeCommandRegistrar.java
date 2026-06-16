package dev.rono.igniscore.sponge.command;

import dev.rono.igniscore.command.IgnisCommands;
import org.spongepowered.api.command.Command;
import org.spongepowered.plugin.PluginContainer;

import java.util.ArrayList;
import java.util.List;

public final class SpongeCommandRegistrar {
    private SpongeCommandRegistrar() {
    }

    public static void register(CommandRegistrationSink sink,
                                PluginContainer container,
                                Command.Parameterized command) {
        sink.register(container, command, IgnisCommands.IGNIS);
        for (String alias : IgnisCommands.ALIASES) {
            sink.register(container, command, alias);
        }
    }

    public static void register(CommandRegistrationSink sink,
                                PluginContainer container,
                                SpongeIgnisCommand command) {
        register(sink, container, command.build());
    }

    public static void register(org.spongepowered.api.event.lifecycle.RegisterCommandEvent<Command.Parameterized> event,
                                PluginContainer container,
                                SpongeIgnisCommand command) {
        register(event::register, container, command);
    }

    @FunctionalInterface
    public interface CommandRegistrationSink {
        void register(PluginContainer container, Command.Parameterized command, String label);
    }
}
