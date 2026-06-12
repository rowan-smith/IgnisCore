package dev.rono.igniscore.sponge.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.loader.LoadedExtension;
import dev.rono.igniscore.manager.BlockTypeRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpongeBlockManager implements BlockTypeRegistry {
    private final Map<String, BlockDefinition> blockTypes = new HashMap<>();
    private final Map<IgnisLocation, String> placedBlocks = new ConcurrentHashMap<>();

    @Inject
    public SpongeBlockManager() {
    }

    @Override
    public void loadFromExtensions(List<LoadedExtension<BlockDefinition>> extensions) {
        blockTypes.clear();
        for (LoadedExtension<BlockDefinition> extension : extensions) {
            blockTypes.put(extension.getDefinition().getId(), extension.getDefinition());
        }
    }

    public Map<String, BlockDefinition> getBlockTypes() {
        return Collections.unmodifiableMap(blockTypes);
    }

    public String getPlacedBlockType(IgnisLocation location) {
        if (location == null) {
            return null;
        }
        return placedBlocks.get(normalize(location));
    }

    public void registerPlacedBlock(IgnisLocation location, String typeId) {
        placedBlocks.put(normalize(location), typeId);
    }

    public void unregisterPlacedBlock(IgnisLocation location) {
        placedBlocks.remove(normalize(location));
    }

    public RuntimeBlockInstance triggerBlock(IgnisLocation location, String typeId, Object context) {
        return null;
    }

    public Collection<RuntimeBlockInstance> getActiveBlocks() {
        return List.of();
    }

    public void cleanup() {
        placedBlocks.clear();
    }

    private static IgnisLocation normalize(IgnisLocation location) {
        return new IgnisLocation(location.worldName(),
                Math.floor(location.x()) + 0.5,
                Math.floor(location.y()),
                Math.floor(location.z()) + 0.5);
    }
}
