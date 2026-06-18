package dev.rono.igniscore.sponge.command;

import dev.rono.igniscore.command.IgnisCommands;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpongeCommandRegistrarTest {

    @Test
    void registersPrimaryCommandAndAliases() {
        List<String> labels = new ArrayList<>();

        SpongeCommandRegistrar.register(
                (pluginContainer, built, label) -> labels.add(label),
                null,
                new NoOpCommand());

        assertEquals(IgnisCommands.ALIASES.size() + 1, labels.size());
        assertTrue(labels.contains("ignis"));
        assertTrue(labels.contains("ic"));
    }

    private static final class NoOpCommand implements Command.Parameterized {
        @Override
        public List<Flag> flags() {
            return List.of();
        }

        @Override
        public List<Parameter> parameters() {
            return List.of();
        }

        @Override
        public List<Parameter.Subcommand> subcommands() {
            return List.of();
        }

        @Override
        public boolean isTerminal() {
            return false;
        }

        @Override
        public Predicate<CommandCause> executionRequirements() {
            return cause -> true;
        }

        @Override
        public CommandContext parseArguments(CommandCause cause, ArgumentReader.Mutable reader)
                throws ArgumentParseException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<CommandExecutor> executor() {
            return Optional.empty();
        }

        @Override
        public CommandResult process(CommandCause cause, ArgumentReader.Mutable arguments) {
            return CommandResult.success();
        }

        @Override
        public List<CommandCompletion> complete(CommandCause cause, ArgumentReader.Mutable arguments) {
            return List.of();
        }

        @Override
        public boolean canExecute(CommandCause cause) {
            return true;
        }

        @Override
        public Optional<Component> shortDescription(CommandCause cause) {
            return Optional.empty();
        }

        @Override
        public Optional<Component> extendedDescription(CommandCause cause) {
            return Optional.empty();
        }

        @Override
        public Component usage(CommandCause cause) {
            return Component.empty();
        }
    }
}
