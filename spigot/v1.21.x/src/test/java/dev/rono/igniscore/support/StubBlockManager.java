package dev.rono.igniscore.support;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.util.Locations;
import dev.rono.igniscore.loader.LoadedExtension;
import dev.rono.igniscore.manager.BlockTypeRegistry;
import dev.rono.igniscore.manager.PlacedBlockRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class StubBlockManager implements BlockTypeRegistry, PlacedBlockRegistry {
    private final Map<String, BlockDefinition> blockTypes;
    private final Map<IgnisLocation, String> placedBlocks = new ConcurrentHashMap<>();

    private StubBlockManager(Map<String, BlockDefinition> blockTypes) {
        this.blockTypes = blockTypes;
    }

    public static StubBlockManager with(BlockDefinition... definitions) {
        Map<String, BlockDefinition> types = new HashMap<>();
        for (BlockDefinition definition : definitions) {
            types.put(definition.getId(), definition);
        }
        return new StubBlockManager(Map.copyOf(types));
    }

    @Override
    public void loadFromExtensions(List<LoadedExtension<BlockDefinition>> extensions) {
    }

    @Override
    public Map<String, BlockDefinition> getBlockTypes() {
        return blockTypes;
    }

    @Override
    public void registerPlacedBlock(IgnisLocation location, String typeId, IgnisItem placedFrom) {
        placedBlocks.put(Locations.toBlock(location), typeId);
    }

    @Override
    public String getPlacedBlockType(IgnisLocation location) {
        return placedBlocks.get(Locations.toBlock(location));
    }
}
