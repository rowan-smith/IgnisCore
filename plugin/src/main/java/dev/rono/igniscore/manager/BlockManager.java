package dev.rono.igniscore.manager;

import com.google.inject.Inject;
import dev.rono.igniscore.Main;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.loader.LoadedBlockExtension;
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
    private final ConfiguredEffectService effectService;
    private final StrategyProfileResolver profileResolver;
    private final Map<String, BlockDefinition> blockTypes = new HashMap<>();
    private final Map<Location, String> placedBlocks = new ConcurrentHashMap<>();
    private final Map<Location, org.bukkit.entity.Display> blockVisuals = new ConcurrentHashMap<>();
    private final BlockDisplayRenderer renderer;

    @Inject
    public BlockManager(Main plugin,
                        IgnisStrategyRegistry strategyRegistry,
                        ConfiguredEffectService effectService,
                        StrategyProfileResolver profileResolver) {
        this.plugin = plugin;
        this.strategyRegistry = strategyRegistry;
        this.effectService = effectService;
        this.profileResolver = profileResolver;
        this.renderer = new BlockDisplayRenderer(plugin);
    }

    public void loadFromExtensions(List<LoadedBlockExtension> extensions) {
        blockTypes.clear();
        for (LoadedBlockExtension extension : extensions) {
            BlockDefinition definition = extension.getBlockDefinition();
            blockTypes.put(definition.getId(), definition);
        }
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
        } else if ("entity".equals(strategyName) && "spider-storm".equalsIgnoreCase(type.getId())) {
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
        if (type == null) {
            return null;
        }

        RuntimeBlockInstance instance = plugin.getRuntimeBlockService().createInstance(type, location);
        renderer.spawnDisplay(instance);
        strategyRegistry.get(type.getStrategy()).onPlace(instance);

        instance.setTask(Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            instance.tick();
            renderer.updateAnimation(instance);
            strategyRegistry.get(type.getStrategy()).onTick(instance);

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
