package dev.rono.igniscore.manager;

import com.google.inject.Inject;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.api.strategy.IgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.loader.LoadedExtension;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.util.Locations;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.renderer.BlockDisplayRenderer;
import dev.rono.igniscore.service.ConfiguredEffectService;
import dev.rono.igniscore.service.PlacedBlockPersistenceService;
import dev.rono.igniscore.service.StrategyProfileResolver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlockManager {
    private final Main plugin;
    private final IgnisStrategyRegistry strategyRegistry;
    private final ConfiguredEffectService effectService;
    private final StrategyProfileResolver profileResolver;
    private final PlacedBlockPersistenceService placedBlockPersistence;
    private final Map<String, BlockDefinition> blockTypes = new HashMap<>();
    private final Map<Location, String> placedBlocks = new ConcurrentHashMap<>();
    private final Map<Location, org.bukkit.entity.Display> blockVisuals = new ConcurrentHashMap<>();
    private final BlockDisplayRenderer renderer;

    @Inject
    public BlockManager(Main plugin,
                        IgnisStrategyRegistry strategyRegistry,
                        ConfiguredEffectService effectService,
                        StrategyProfileResolver profileResolver,
                        PlacedBlockPersistenceService placedBlockPersistence) {
        this.plugin = plugin;
        this.strategyRegistry = strategyRegistry;
        this.effectService = effectService;
        this.profileResolver = profileResolver;
        this.placedBlockPersistence = placedBlockPersistence;
        this.renderer = new BlockDisplayRenderer(plugin);
    }

    public void loadFromExtensions(List<LoadedExtension<BlockDefinition>> extensions) {
        blockTypes.clear();
        for (LoadedExtension<BlockDefinition> extension : extensions) {
            BlockDefinition definition = extension.getDefinition();
            blockTypes.put(definition.getId(), definition);
        }
    }

    public void registerPlacedBlock(Location location, String typeId) {
        registerPlacedBlock(location, typeId, null);
    }

    public void registerPlacedBlock(Location location, String typeId, ItemStack placedFrom) {
        Location blockLocation = location.getBlock().getLocation();
        placedBlocks.put(blockLocation, typeId);
        if (placedBlockPersistence != null) {
            placedBlockPersistence.recordPlacement(blockLocation, typeId);
        }

        BlockDefinition type = blockTypes.get(typeId);
        if (type != null) {
            org.bukkit.entity.Display display = renderer.spawnStaticDisplay(blockLocation, type);
            blockVisuals.put(blockLocation, display);
            playPlacementEffects(blockLocation, type, placedFrom);
        }
    }

    public void restorePlacedBlock(Location location, String typeId) {
        Location blockLocation = location.getBlock().getLocation();
        if (placedBlocks.containsKey(blockLocation)) {
            return;
        }

        placedBlocks.put(blockLocation, typeId);
        BlockDefinition type = blockTypes.get(typeId);
        if (type == null) {
            return;
        }

        org.bukkit.entity.Display display = renderer.spawnStaticDisplay(blockLocation, type);
        blockVisuals.put(blockLocation, display);
        requireBlockStrategy(type).onStaticPlace(type, blockLocation, null);
    }

    private void playPlacementEffects(Location location, BlockDefinition type, ItemStack placedFrom) {
        StrategyProfile profile = profileResolver.resolve(type);
        Location center = Locations.toCenter(location);

        if (profile.getPlacementSound() != null) {
            effectService.playSound(center, profile.getPlacementSound(), 1.6f, 0.7f);
        }

        requireBlockStrategy(type).onStaticPlace(type, location, placedFrom);
    }

    public void unregisterPlacedBlock(Location location) {
        Location blockLocation = location.getBlock().getLocation();
        placedBlocks.remove(blockLocation);
        if (placedBlockPersistence != null) {
            placedBlockPersistence.removePlacement(blockLocation);
        }
        org.bukkit.entity.Display display = blockVisuals.remove(blockLocation);
        if (display != null) {
            display.remove();
        }
    }

    public String getPlacedBlockType(Location location) {
        return placedBlocks.get(location.getBlock().getLocation());
    }

    public RuntimeBlockInstance triggerBlock(Location location, String typeId, Object context) {
        BlockDefinition type = blockTypes.get(typeId);
        if (type == null) {
            return null;
        }

        RuntimeBlockInstance instance = plugin.getRuntimeBlockService().createInstance(type, location);
        renderer.spawnDisplay(instance);
        IgnisBlockStrategy strategy = requireBlockStrategy(type);
        strategy.onPlace(instance);

        instance.setTask(Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            instance.tick();
            renderer.updateAnimation(instance);
            strategy.onTick(instance);

            if (instance.getTicksLeft() <= 0) {
                executeBehavior(instance, context);
            }
        }, 1L, 1L));

        return instance;
    }

    public void executeBehavior(RuntimeBlockInstance instance, Object context) {
        if (instance.getTask() != null) {
            instance.getTask().cancel();
        }
        plugin.getRuntimeBlockService().removeInstance(instance.getUuid());
        renderer.removeDisplay(instance);
        requireBlockStrategy(instance.getDefinition()).onTrigger(instance, context);
    }

    private IgnisBlockStrategy requireBlockStrategy(BlockDefinition definition) {
        IgnisStrategy strategy = strategyRegistry.get(definition.getExtensionId());
        if (!(strategy instanceof IgnisBlockStrategy blockStrategy)) {
            throw new IllegalStateException("Block type " + definition.getId() + " uses a non-block strategy from extension "
                    + definition.getExtensionId());
        }
        return blockStrategy;
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
            if (instance.getTask() != null) {
                instance.getTask().cancel();
            }
            renderer.removeDisplay(instance);
        }

        for (org.bukkit.entity.Display display : blockVisuals.values()) {
            display.remove();
        }
        blockVisuals.clear();
        placedBlocks.clear();
    }
}
