package dev.rono.igniscore.manager;

import dev.rono.igniscore.Main;
import dev.rono.igniscore.core.BlockBehaviorRegistry;
import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.BlockInstance;
import dev.rono.igniscore.renderer.BlockDisplayRenderer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlockManager {
    private final Main plugin;
    private final Map<String, BlockDefinition> blockTypes = new HashMap<>();
    private final Map<UUID, BlockInstance> activeBlocks = new ConcurrentHashMap<>();
    private final Map<Location, String> placedBlocks = new ConcurrentHashMap<>();
    private final Map<Location, org.bukkit.entity.Display> blockVisuals = new ConcurrentHashMap<>();
    private final BlockDisplayRenderer renderer;
    private final BlockDefinitionLoader loader;

    public BlockManager(Main plugin) {
        this.plugin = plugin;
        this.renderer = new BlockDisplayRenderer(plugin);
        this.loader = new BlockDefinitionLoader(plugin);
        BlockBehaviorRegistry.init(this);
        loadConfig();
    }

    public void loadConfig() {
        blockTypes.clear();
        blockTypes.putAll(loader.loadDefinitions());
    }

    public void registerPlacedBlock(Location location, String typeId) {
        placedBlocks.put(location, typeId);
        BlockDefinition type = blockTypes.get(typeId);
        if (type != null) {
            org.bukkit.entity.Display display = renderer.spawnStaticDisplay(location, type);
            blockVisuals.put(location, display);
        }
    }

    public void unregisterPlacedBlock(Location location) {
        placedBlocks.remove(location);
        org.bukkit.entity.Display display = blockVisuals.remove(location);
        if (display != null) {
            display.remove();
        }
    }

    public String getPlacedBlockType(Location location) {
        return placedBlocks.get(location);
    }

    public BlockInstance triggerBlock(Location location, String typeId, Object context) {
        BlockDefinition type = blockTypes.get(typeId);
        if (type == null) return null;
        
        BlockInstance instance = new BlockInstance(location, type);
        renderer.spawnDisplay(instance);
        activeBlocks.put(instance.getUuid(), instance);
        
        BlockBehaviorRegistry.get(type.getStrategy()).onPlace(instance);

        // Individual scheduler for this instance
        instance.setTask(Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            instance.tick();
            renderer.updateAnimation(instance);
            BlockBehaviorRegistry.get(type.getStrategy()).onTick(instance);
            
            if (instance.getTicksLeft() <= 0) {
                executeBehavior(instance, context);
            }
        }, 1L, 1L));
        
        return instance;
    }

    public void executeBehavior(BlockInstance instance, Object context) {
        if (instance.getTask() != null) instance.getTask().cancel();
        activeBlocks.remove(instance.getUuid());
        renderer.removeDisplay(instance);
        BlockBehaviorRegistry.get(instance.getType().getStrategy()).onTrigger(instance, context);
    }

    public Map<String, BlockDefinition> getBlockTypes() {
        return Collections.unmodifiableMap(blockTypes);
    }

    public Collection<BlockInstance> getActiveBlocks() {
        return activeBlocks.values();
    }
    
    public void cleanup() {
        for (BlockInstance instance : activeBlocks.values()) {
            if (instance.getTask() != null) instance.getTask().cancel();
            renderer.removeDisplay(instance);
        }
        activeBlocks.clear();
        
        for (org.bukkit.entity.Display display : blockVisuals.values()) {
            display.remove();
        }
        blockVisuals.clear();
        placedBlocks.clear();
    }
}
