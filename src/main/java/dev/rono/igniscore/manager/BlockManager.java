package dev.rono.igniscore.manager;

import com.google.inject.Inject;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.api.strategy.IgnisStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.loader.ContentPackLoader;
import dev.rono.igniscore.loader.LoadedContentPack;
import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.RuntimeBlockInstance;
import dev.rono.igniscore.renderer.BlockDisplayRenderer;
import dev.rono.igniscore.service.ConfiguredEffectService;
import dev.rono.igniscore.service.StrategyProfileResolver;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlockManager {
    private final Main plugin;
    private final IgnisStrategyRegistry strategyRegistry;
    private final ContentPackLoader contentPackLoader;
    private final ConfiguredEffectService effectService;
    private final StrategyProfileResolver profileResolver;
    private final Map<String, BlockDefinition> blockTypes = new HashMap<>();
    private final Map<Location, String> placedBlocks = new ConcurrentHashMap<>();
    private final Map<Location, org.bukkit.entity.Display> blockVisuals = new ConcurrentHashMap<>();
    private final BlockDisplayRenderer renderer;
    private final BlockDefinitionLoader loader;

    @Inject
    public BlockManager(Main plugin,
                        IgnisStrategyRegistry strategyRegistry,
                        ContentPackLoader contentPackLoader,
                        ConfiguredEffectService effectService,
                        StrategyProfileResolver profileResolver) {
        this.plugin = plugin;
        this.strategyRegistry = strategyRegistry;
        this.contentPackLoader = contentPackLoader;
        this.effectService = effectService;
        this.profileResolver = profileResolver;
        this.renderer = new BlockDisplayRenderer(plugin);
        this.loader = new BlockDefinitionLoader(plugin);
    }

    public void loadConfig() {
        blockTypes.clear();
        plugin.reloadConfig();

        List<String> registeredIds = new ArrayList<>(plugin.getConfig().getStringList("blocks"));
        for (LoadedContentPack pack : contentPackLoader.getLoadedPacks()) {
            for (String blockId : pack.getManifest().getBlocks()) {
                if (!registeredIds.contains(blockId)) {
                    registeredIds.add(blockId);
                }
            }
        }

        blockTypes.putAll(loader.loadDefinitions(registeredIds, contentPackLoader.getLoadedPacks()));
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
        StrategyProfile profile = profileResolver.resolve(type);
        Location center = location.toCenterLocation();

        if (profile.getPlacementSound() != null) {
            effectService.playSound(center, profile.getPlacementSound(), 1.6f, 0.7f);
        }

        String strategyName = type.getStrategy().toLowerCase(Locale.ROOT);
        if ("nuclear".equals(strategyName)) {
            center.getWorld().spawnParticle(Particle.FLAME, center, 16, 0.35, 0.35, 0.35, 0.02);
            center.getWorld().spawnParticle(Particle.SMOKE, center, 10, 0.3, 0.3, 0.3, 0.01);
        } else         if ("entity".equals(strategyName) && "spider-storm".equalsIgnoreCase(type.getId())) {
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

        IgnisStrategy strategy = strategyRegistry.get(type.getStrategy());
        RuntimeBlockInstance instance = plugin.getRuntimeBlockService().createInstance(type, location);
        renderer.spawnDisplay(instance);

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
        if (instance.getTask() != null) instance.getTask().cancel();
        plugin.getRuntimeBlockService().removeInstance(instance.getUuid());
        renderer.removeDisplay(instance);
        strategyRegistry.get(instance.getDefinition().getStrategy()).onTrigger(instance, context);
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

        for (org.bukkit.entity.Display display : blockVisuals.values()) {
            display.remove();
        }
        blockVisuals.clear();
        placedBlocks.clear();
    }
}
