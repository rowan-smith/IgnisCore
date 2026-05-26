package dev.rono.igniscore.core;

import dev.rono.igniscore.Main;
import dev.rono.igniscore.manager.TNTManager;
import dev.rono.igniscore.model.TNTInstance;
import dev.rono.igniscore.model.TNTDefinition;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;

public class ExplosionHandler {
    private final Main plugin;
    private final TNTManager manager;

    public ExplosionHandler(Main plugin, TNTManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    public void handleExplosion(TNTInstance instance) {
        Location loc = instance.getLocation();
        TNTDefinition type = instance.getType();
        World world = loc.getWorld();

        // Standard explosion effect
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        
        // Use strategy from registry
        ExplosionStrategy strategy = ExplosionStrategyRegistry.get(type.getExplosionType());
        strategy.execute(loc, type);
    }
}
