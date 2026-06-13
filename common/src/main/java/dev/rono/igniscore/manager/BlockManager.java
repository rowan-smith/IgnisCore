package dev.rono.igniscore.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.BlockVisualRenderer;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.strategy.IgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.util.Locations;
import dev.rono.igniscore.loader.LoadedExtension;
import dev.rono.igniscore.service.PlacedBlockPersistenceService;
import dev.rono.igniscore.service.RuntimeBlockService;
import dev.rono.igniscore.service.StrategyProfileResolver;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class BlockManager implements BlockTypeRegistry, PlacedBlockRegistry {
    private final RuntimeBlockService runtimeBlockService;
    private final IgnisStrategyRegistry strategyRegistry;
    private final IgnisEffectService effectService;
    private final StrategyProfileResolver profileResolver;
    private final PlacedBlockPersistenceService placedBlockPersistence;
    private final IgnisScheduler scheduler;
    private final BlockVisualRenderer visualRenderer;
    private final Map<String, BlockDefinition> blockTypes = new HashMap<>();
    private final Map<IgnisLocation, String> placedBlocks = new ConcurrentHashMap<>();
    private final Map<IgnisLocation, Object> blockVisuals = new ConcurrentHashMap<>();

    @Inject
    public BlockManager(RuntimeBlockService runtimeBlockService,
                        IgnisStrategyRegistry strategyRegistry,
                        IgnisEffectService effectService,
                        StrategyProfileResolver profileResolver,
                        PlacedBlockPersistenceService placedBlockPersistence,
                        IgnisScheduler scheduler,
                        BlockVisualRenderer visualRenderer) {
        this.runtimeBlockService = runtimeBlockService;
        this.strategyRegistry = strategyRegistry;
        this.effectService = effectService;
        this.profileResolver = profileResolver;
        this.placedBlockPersistence = placedBlockPersistence;
        this.scheduler = scheduler;
        this.visualRenderer = visualRenderer;
    }

    @Override
    public void loadFromExtensions(List<LoadedExtension<BlockDefinition>> extensions) {
        blockTypes.clear();
        for (LoadedExtension<BlockDefinition> extension : extensions) {
            BlockDefinition definition = extension.getDefinition();
            blockTypes.put(definition.getId(), definition);
        }
    }

    public void registerPlacedBlock(IgnisLocation location, String typeId) {
        registerPlacedBlock(location, typeId, null);
    }

    public void registerPlacedBlock(IgnisLocation location, String typeId, IgnisItem placedFrom) {
        IgnisLocation blockLocation = blockKey(location);
        placedBlocks.put(blockLocation, typeId);
        placedBlockPersistence.recordPlacement(blockLocation, typeId);

        BlockDefinition type = blockTypes.get(typeId);
        if (type != null) {
            Object display = visualRenderer.spawnStaticDisplay(blockLocation, type);
            blockVisuals.put(blockLocation, display);
            playPlacementEffects(blockLocation, type, placedFrom);
        }
    }

    public void restorePlacedBlock(IgnisLocation location, String typeId) {
        IgnisLocation blockLocation = blockKey(location);
        if (placedBlocks.containsKey(blockLocation)) {
            return;
        }

        placedBlocks.put(blockLocation, typeId);
        BlockDefinition type = blockTypes.get(typeId);
        if (type == null) {
            return;
        }

        Object display = visualRenderer.spawnStaticDisplay(blockLocation, type);
        blockVisuals.put(blockLocation, display);
        requireBlockStrategy(type).onPlaced(type, blockLocation, null);
    }

    private void playPlacementEffects(IgnisLocation location, BlockDefinition type, IgnisItem placedFrom) {
        StrategyProfile profile = profileResolver.resolve(type);
        IgnisLocation center = Locations.toCenter(location);

        if (profile.getPlacementSound() != null) {
            effectService.playSound(center, profile.getPlacementSound(), 1.6f, 0.7f);
        }

        requireBlockStrategy(type).onPlaced(type, location, placedFrom);
    }

    public void unregisterPlacedBlock(IgnisLocation location) {
        IgnisLocation blockLocation = blockKey(location);
        placedBlocks.remove(blockLocation);
        placedBlockPersistence.removePlacement(blockLocation);
        Object display = blockVisuals.remove(blockLocation);
        if (display != null) {
            visualRenderer.removeStaticDisplay(display);
        }
    }

    public String getPlacedBlockType(IgnisLocation location) {
        return placedBlocks.get(blockKey(location));
    }

    public RuntimeBlockInstance triggerBlock(IgnisLocation location, String typeId, Object context) {
        BlockDefinition type = blockTypes.get(typeId);
        if (type == null) {
            return null;
        }

        RuntimeBlockInstance instance = runtimeBlockService.createInstance(type, location);
        visualRenderer.spawnAnimatedDisplay(instance);
        IgnisBlockStrategy strategy = requireBlockStrategy(type);
        strategy.onPlace(instance);

        instance.setTask(scheduler.runRepeating(instance.getLocation(), () -> {
            instance.tick();
            visualRenderer.updateAnimation(instance);
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
        runtimeBlockService.removeInstance(instance.getUuid());
        visualRenderer.removeDisplay(instance);
        requireBlockStrategy(instance.getDefinition()).onTrigger(instance, context);
    }

    private IgnisBlockStrategy requireBlockStrategy(BlockDefinition definition) {
        return strategyRegistry.requireBlockStrategy(definition.getExtensionId(), definition.getId());
    }

    public Map<String, BlockDefinition> getBlockTypes() {
        return Collections.unmodifiableMap(blockTypes);
    }

    public Collection<RuntimeBlockInstance> getActiveBlocks() {
        return runtimeBlockService.getActiveInstances();
    }

    public void cleanup() {
        stopActiveBlocks();
        for (Object display : blockVisuals.values()) {
            visualRenderer.removeStaticDisplay(display);
        }
        blockVisuals.clear();
        placedBlocks.clear();
    }

    public void stopActiveBlocks() {
        for (RuntimeBlockInstance instance : runtimeBlockService.getActiveInstances()) {
            if (instance.getTask() != null) {
                instance.getTask().cancel();
            }
            visualRenderer.removeDisplay(instance);
        }
        runtimeBlockService.clearAll();
    }

    public void refreshPlacedBlockVisuals() {
        for (Map.Entry<IgnisLocation, String> entry : Map.copyOf(placedBlocks).entrySet()) {
            IgnisLocation location = entry.getKey();
            Object existing = blockVisuals.remove(location);
            if (existing != null) {
                visualRenderer.removeStaticDisplay(existing);
            }

            BlockDefinition type = blockTypes.get(entry.getValue());
            if (type == null) {
                continue;
            }

            Object display = visualRenderer.spawnStaticDisplay(location, type);
            blockVisuals.put(location, display);
        }
    }

    private static IgnisLocation blockKey(IgnisLocation location) {
        return Locations.toBlock(location);
    }
}
