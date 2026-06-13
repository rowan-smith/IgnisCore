package dev.rono.igniscore.manager;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.loader.LoadedExtension;

import java.util.List;

public interface BlockTypeRegistry {

    void loadFromExtensions(List<LoadedExtension<BlockDefinition>> extensions);
}
