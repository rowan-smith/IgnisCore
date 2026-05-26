package dev.rono.igniscore.core;

import dev.rono.igniscore.model.TNTDefinition;
import org.bukkit.Location;

public interface ExplosionStrategy {
    void execute(Location location, TNTDefinition definition);
}
