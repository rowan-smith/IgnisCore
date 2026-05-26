package dev.rono.igniscore.core;

import dev.rono.igniscore.manager.TNTManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ExplosionStrategyRegistry {
    private static final Map<String, ExplosionStrategy> strategies = new HashMap<>();

    public static void register(String type, ExplosionStrategy strategy) {
        strategies.put(type.toLowerCase(), strategy);
    }

    public static ExplosionStrategy get(String type) {
        return strategies.getOrDefault(type.toLowerCase(), strategies.get("default"));
    }

    public static void init(TNTManager manager) {
        register("default", (loc, def) -> {
            float power = (float) (def.getPower() * def.getMultiplier());
            loc.getWorld().createExplosion(loc, power, def.isFire(), def.isBlockDamage());
        });

        register("nuclear", (loc, def) -> {
            double basePower = def.getRadius() > 0 ? def.getRadius() : def.getPower();
            float finalPower = (float) (basePower * def.getMultiplier());
            
            loc.getWorld().createExplosion(loc, finalPower, def.isFire(), def.isBlockDamage());
            
            if (def.isScreenShake()) {
                loc.getWorld().getPlayers().stream()
                    .filter(p -> p.getLocation().distance(loc) < finalPower * 2)
                    .forEach(p -> p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f));
            }
        });

        register("entity", (loc, def) -> {
            double basePower = def.getRadius() > 0 ? def.getRadius() : def.getPower();
            float finalPower = (float) (basePower * def.getMultiplier());
            
            loc.getWorld().createExplosion(loc, finalPower, def.isFire(), def.isBlockDamage());
            
            if (def.getEntityPayloadType() != null) {
                try {
                    EntityType type = EntityType.valueOf(def.getEntityPayloadType().toUpperCase());
                    int count = def.getEntityPayloadCount();
                    for (int i = 0; i < count; i++) {
                        org.bukkit.entity.Entity entity = loc.getWorld().spawnEntity(loc.clone().add(Math.random() * 2 - 1, 0, Math.random() * 2 - 1), type);
                        
                        if (def.isEntityPayloadTargetPlayers() && entity instanceof Mob mob) {
                            loc.getWorld().getPlayers().stream()
                                    .min(Comparator.comparingDouble(p -> p.getLocation().distanceSquared(loc)))
                                    .ifPresent(mob::setTarget);
                        }
                    }
                } catch (IllegalArgumentException e) {
                    // Invalid entity type
                }
            }
        });
    }
}
