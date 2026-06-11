package dev.rono.igniscore.support;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.manager.BlockManager;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class StubBlockManager extends BlockManager {
    private final Map<String, BlockDefinition> blockTypes;
    private final Map<Location, String> placedBlocks = new ConcurrentHashMap<>();

    private StubBlockManager(Map<String, BlockDefinition> blockTypes) {
        super(null, null, null, null);
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
    public Map<String, BlockDefinition> getBlockTypes() {
        return blockTypes;
    }

    @Override
    public void registerPlacedBlock(Location location, String typeId) {
        placedBlocks.put(location, typeId);
    }

    @Override
    public String getPlacedBlockType(Location location) {
        return placedBlocks.get(location);
    }
}
