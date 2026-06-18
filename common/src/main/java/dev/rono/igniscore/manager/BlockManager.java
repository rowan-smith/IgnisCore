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
import dev.rono.igniscore.api.config.BlockBehaviorConfig;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;
import dev.rono.igniscore.api.util.PlacedMetaSupport;
import dev.rono.igniscore.config.PerformanceSettings;
import dev.rono.igniscore.event.StrategyEventPublisher;
import dev.rono.igniscore.loader.LoadedExtension;
import dev.rono.igniscore.service.PlacedBlockPersistenceService;
import dev.rono.igniscore.service.RuntimeBlockService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class BlockManager implements BlockTypeRegistry, PlacedBlockRegistry {
    private final RuntimeBlockService runtimeBlockService;
    private final IgnisEffectService effectService;
    private final PlacedBlockPersistenceService placedBlockPersistence;
    private final IgnisScheduler scheduler;
    private final BlockVisualRenderer visualRenderer;
    private final StrategyEventPublisher events;
    private final int visualRefreshBatchSize;
    private final Map<String, BlockDefinition> blockTypes = new ConcurrentHashMap<>();
    private final Map<IgnisLocation, String> placedBlocks = new ConcurrentHashMap<>();
    private final Map<IgnisLocation, Object> blockVisuals = new ConcurrentHashMap<>();

    @Inject
    public BlockManager(RuntimeBlockService runtimeBlockService,
                        IgnisEffectService effectService,
                        PlacedBlockPersistenceService placedBlockPersistence,
                        IgnisScheduler scheduler,
                        BlockVisualRenderer visualRenderer,
                        StrategyEventPublisher events,
                        PerformanceSettings performanceSettings) {
        this.runtimeBlockService = runtimeBlockService;
        this.effectService = effectService;
        this.placedBlockPersistence = placedBlockPersistence;
        this.scheduler = scheduler;
        this.visualRenderer = visualRenderer;
        this.events = events;
        this.visualRefreshBatchSize = performanceSettings.visualRefreshBlocksPerTick();
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
        events.fireBlockPlace(type, blockLocation, null);
    }

    private void playPlacementEffects(IgnisLocation location, BlockDefinition type, IgnisItem placedFrom) {
        BlockBehaviorConfig behavior = BlockBehaviorConfig.from(type.getBehaviorConfig());
        IgnisLocation center = Locations.toCenter(location);

        if (behavior.placementSound() != null) {
            effectService.playSound(center, behavior.placementSound(), 1.6f, 0.7f);
        }

        events.fireBlockPlace(type, location, placedFrom);
    }

    public void unregisterPlacedBlock(IgnisLocation location) {
        IgnisLocation blockLocation = blockKey(location);
        PlacedMetaSupport.clear(blockLocation);
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
        instance.setTicksLeft(StrategySupport.customInt(type, "fuse", 0));
        visualRenderer.spawnAnimatedDisplay(instance);
        events.fireBlockActivate(instance);

        instance.setTask(scheduler.runRepeating(instance.getLocation(), () -> {
            instance.tick();
            visualRenderer.updateAnimation(instance);
            events.fireBlockTick(instance);

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
        events.fireBlockTrigger(instance, context);
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
        List<Map.Entry<IgnisLocation, String>> entries = new ArrayList<>(Map.copyOf(placedBlocks).entrySet());
        if (entries.isEmpty()) {
            return;
        }
        refreshPlacedBlockVisuals(entries, 0);
    }

    private void refreshPlacedBlockVisuals(List<Map.Entry<IgnisLocation, String>> entries, int startIndex) {
        int endIndex = Math.min(startIndex + visualRefreshBatchSize, entries.size());
        for (int index = startIndex; index < endIndex; index++) {
            refreshPlacedBlockVisual(entries.get(index));
        }

        if (endIndex < entries.size()) {
            scheduler.runGlobalLater(() -> refreshPlacedBlockVisuals(entries, endIndex), 1L);
        }
    }

    private void refreshPlacedBlockVisual(Map.Entry<IgnisLocation, String> entry) {
        IgnisLocation location = entry.getKey();
        Object existing = blockVisuals.remove(location);
        if (existing != null) {
            visualRenderer.removeStaticDisplay(existing);
        }

        BlockDefinition type = blockTypes.get(entry.getValue());
        if (type == null) {
            return;
        }

        Object display = visualRenderer.spawnStaticDisplay(location, type);
        blockVisuals.put(location, display);
    }

    private static IgnisLocation blockKey(IgnisLocation location) {
        return Locations.toBlock(location);
    }
}
