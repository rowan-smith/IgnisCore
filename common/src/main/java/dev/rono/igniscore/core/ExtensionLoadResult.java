package dev.rono.igniscore.core;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.loader.LoadedExtension;

import java.util.List;

public record ExtensionLoadResult(
        List<LoadedExtension<BlockDefinition>> blocks,
        List<LoadedExtension<ItemDefinition>> items) {
}
