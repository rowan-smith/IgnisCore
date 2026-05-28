package dev.rono.igniscore.manager;

import com.google.inject.Inject;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.core.BlockBehaviorRegistry;
import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.RuntimeBlockInstance;
import dev.rono.igniscore.renderer.BlockDisplayRenderer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlockManager {
    private final Main plugin;
    private final Map<String, BlockDefinition> blockTypes = new HashMap<>();
    private final Map<Location, String> placedBlocks = new ConcurrentHashMap<>();
    private final Map<Location, org.bukkit.entity.Display> blockVisuals = new ConcurrentHashMap<>();
    private final BlockDisplayRenderer renderer;
    private final BlockDefinitionLoader loader;

    @Inject
    public BlockManager(Main plugin) {
        this.plugin = plugin;
        this.renderer = new BlockDisplayRenderer(plugin);
        this.loader = new BlockDefinitionLoader(plugin);
        BlockBehaviorRegistry.init();
        loadConfig();
    }

    public void loadConfig() {
        blockTypes.clear();
        plugin.reloadConfig();
        List<String> registeredIds = plugin.getConfig().getStringList("blocks");
        blockTypes.putAll(loader.loadDefinitions(registeredIds));
    }

    public void registerPlacedBlock(Location location, String typeId) {
        placedBlocks.put(location, typeId);
        BlockDefinition type = blockTypes.get(typeId);
        if (type != null) {
            org.bukkit.entity.Display display = renderer.spawnStaticDisplay(location, type);
            blockVisuals.put(location, display);
            playPlacementEffects(location, type);
        }
    }

    private void playPlacementEffects(Location location, BlockDefinition type) {
        Location center = location.toCenterLocation();
        String strategy = type.getStrategy().toLowerCase(Locale.ROOT);
        if ("nuclear".equals(strategy)) {
            center.getWorld().playSound(center, Sound.BLOCK_BEACON_ACTIVATE, 1.6f, 0.7f);
            center.getWorld().spawnParticle(Particle.FLAME, center, 16, 0.35, 0.35, 0.35, 0.02);
            center.getWorld().spawnParticle(Particle.SMOKE, center, 10, 0.3, 0.3, 0.3, 0.01);
            return;
        }

        if ("entity".equals(strategy) && "spider-storm".equalsIgnoreCase(type.getId())) {
            center.getWorld().playSound(center, Sound.ENTITY_SPIDER_AMBIENT, 1.2f, 0.8f);
            center.getWorld().spawnParticle(Particle.SPORE_BLOSSOM_AIR, center, 18, 0.45, 0.45, 0.45, 0.01);
            center.getWorld().spawnParticle(Particle.SMOKE, center, 8, 0.3, 0.3, 0.3, 0.01);
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

    public RuntimeBlockInstance triggerBlock(Location location, String typeId, Object context) {
        BlockDefinition type = blockTypes.get(typeId);
        if (type == null) return null;
        
        RuntimeBlockInstance instance = plugin.getRuntimeBlockService().createInstance(type, location);
        renderer.spawnDisplay(instance);
        
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

    public void executeBehavior(RuntimeBlockInstance instance, Object context) {
        if (instance.getTask() != null) instance.getTask().cancel();
        plugin.getRuntimeBlockService().removeInstance(instance.getUuid());
        renderer.removeDisplay(instance);
        BlockBehaviorRegistry.get(instance.getDefinition().getStrategy()).onTrigger(instance, context);
    }

    public Main getPlugin() {
        return plugin;
    }

    public Map<String, BlockDefinition> getBlockTypes() {
        return Collections.unmodifiableMap(blockTypes);
    }

    public Collection<RuntimeBlockInstance> getActiveBlocks() {
        return plugin.getRuntimeBlockService().getActiveInstances();
    }
    
    public void cleanup() {
        for (RuntimeBlockInstance instance : plugin.getRuntimeBlockService().getActiveInstances()) {
            if (instance.getTask() != null) instance.getTask().cancel();
            renderer.removeDisplay(instance);
        }
        // Note: we don't clear the service here as it might be used by others, 
        // but since this is plugin cleanup it's fine.
        
        for (org.bukkit.entity.Display display : blockVisuals.values()) {
            display.remove();
        }
        blockVisuals.clear();
        placedBlocks.clear();
    }
}
