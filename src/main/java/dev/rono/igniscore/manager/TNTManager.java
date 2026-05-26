package dev.rono.igniscore.manager;

import dev.rono.igniscore.Main;
import dev.rono.igniscore.core.ExplosionHandler;
import dev.rono.igniscore.core.ExplosionStrategyRegistry;
import dev.rono.igniscore.model.TNTDefinition;
import dev.rono.igniscore.model.TNTInstance;
import dev.rono.igniscore.renderer.BlockDisplayRenderer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TNTManager {
    private final Main plugin;
    private final Map<String, TNTDefinition> tntTypes = new HashMap<>();
    private final Map<UUID, TNTInstance> activeTnts = new ConcurrentHashMap<>();
    private final BlockDisplayRenderer renderer;
    private final ExplosionHandler explosionHandler;
    private final TNTDefinitionLoader loader;

    public TNTManager(Main plugin) {
        this.plugin = plugin;
        this.renderer = new BlockDisplayRenderer(plugin);
        this.explosionHandler = new ExplosionHandler(plugin, this);
        this.loader = new TNTDefinitionLoader(plugin);
        ExplosionStrategyRegistry.init(this);
        loadConfig();
    }

    public void loadConfig() {
        tntTypes.clear();
        tntTypes.putAll(loader.loadDefinitions());
    }

    public TNTInstance spawnTNT(Location location, String typeId) {
        TNTDefinition type = tntTypes.get(typeId);
        if (type == null) return null;
        
        TNTInstance instance = new TNTInstance(location, type);
        renderer.spawnDisplay(instance);
        activeTnts.put(instance.getUuid(), instance);
        
        // Individual scheduler for this instance
        instance.setTask(Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            instance.tick();
            renderer.updateAnimation(instance);
            
            if (instance.getTicksLeft() <= 0) {
                explode(instance);
            }
        }, 1L, 1L));
        
        return instance;
    }

    public void explode(TNTInstance instance) {
        if (instance.getTask() != null) instance.getTask().cancel();
        activeTnts.remove(instance.getUuid());
        renderer.removeDisplay(instance);
        explosionHandler.handleExplosion(instance);
    }

    public Map<String, TNTDefinition> getTntTypes() {
        return Collections.unmodifiableMap(tntTypes);
    }

    public Collection<TNTInstance> getActiveTNTs() {
        return activeTnts.values();
    }
    
    public void cleanup() {
        for (TNTInstance instance : activeTnts.values()) {
            if (instance.getTask() != null) instance.getTask().cancel();
            renderer.removeDisplay(instance);
        }
        activeTnts.clear();
    }
}
