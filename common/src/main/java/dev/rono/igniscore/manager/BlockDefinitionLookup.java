package dev.rono.igniscore.manager;

import dev.rono.igniscore.api.model.BlockDefinition;

import java.util.Map;

public interface BlockDefinitionLookup {

    Map<String, BlockDefinition> getBlockTypes();
}
