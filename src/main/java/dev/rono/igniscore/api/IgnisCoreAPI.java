package dev.rono.igniscore.api;

import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.model.BlockInstance;
import dev.rono.igniscore.model.BlockDefinition;
import org.bukkit.Location;

import java.util.Collection;
import java.util.Map;

public class IgnisCoreAPI {
    private static BlockManager manager;

    public static void init(BlockManager blockManager) {
        manager = blockManager;
    }

    public static BlockInstance triggerBlock(Location location, String typeId, Object context) {
        return manager.triggerBlock(location, typeId, context);
    }

    public static Collection<BlockInstance> getActiveBlocks() {
        return manager.getActiveBlocks();
    }

    public static Map<String, BlockDefinition> getBlockTypes() {
        return manager.getBlockTypes();
    }
}
