package dev.rono.igniscore.core;

import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.model.BlockDefinition;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class BlockBehaviorRegistry {
    private static final Map<String, BlockBehaviorStrategy> strategies = new HashMap<>();

    public static void register(String type, BlockBehaviorStrategy strategy) {
        strategies.put(type.toLowerCase(), strategy);
    }

    public static BlockBehaviorStrategy get(String type) {
        return strategies.getOrDefault(type.toLowerCase(), strategies.get("default"));
    }

    public static void init(BlockManager manager) {
        // Explosion Strategy Implementation
        BlockBehaviorStrategy explosionBase = new BlockBehaviorStrategy() {
            @Override
            public void onTrigger(dev.rono.igniscore.model.BlockInstance instance, Object context) {
                BlockDefinition def = instance.getType();
                org.bukkit.Location loc = instance.getLocation();
                float power = (float) (getCustomDouble(def, "power", 4.0) * getCustomDouble(def, "multiplier", 1.0));
                loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                loc.getWorld().createExplosion(loc, power, getCustomBoolean(def, "fire", false), getCustomBoolean(def, "blockDamage", true));
            }
        };

        register("default", explosionBase);

        register("nuclear", new BlockBehaviorStrategy() {
            @Override
            public void onTrigger(dev.rono.igniscore.model.BlockInstance instance, Object context) {
                BlockDefinition def = instance.getType();
                org.bukkit.Location loc = instance.getLocation();
                double basePower = def.getRadius() > 0 ? def.getRadius() : getCustomDouble(def, "power", 10.0);
                float finalPower = (float) (basePower * getCustomDouble(def, "multiplier", 1.0));
                
                loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                loc.getWorld().createExplosion(loc, finalPower, getCustomBoolean(def, "fire", true), getCustomBoolean(def, "blockDamage", true));
                
                if (getCustomBoolean(def, "screenShake", false)) {
                    loc.getWorld().getPlayers().stream()
                        .filter(p -> p.getLocation().distance(loc) < finalPower * 2)
                        .forEach(p -> p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f));
                }
            }
        });

        register("entity", new BlockBehaviorStrategy() {
            @Override
            public void onTrigger(dev.rono.igniscore.model.BlockInstance instance, Object context) {
                BlockDefinition def = instance.getType();
                org.bukkit.Location loc = instance.getLocation();
                double basePower = def.getRadius() > 0 ? def.getRadius() : getCustomDouble(def, "power", 4.0);
                float finalPower = (float) (basePower * getCustomDouble(def, "multiplier", 1.0));
                
                loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                loc.getWorld().createExplosion(loc, finalPower, getCustomBoolean(def, "fire", false), getCustomBoolean(def, "blockDamage", true));
                
                Map<String, Object> payload = (Map<String, Object>) def.getCustomData().get("entityPayload");
                if (payload != null) {
                    try {
                        String typeStr = (String) payload.get("type");
                        if (typeStr != null) {
                            EntityType type = EntityType.valueOf(typeStr.toUpperCase());
                            int count = (int) payload.getOrDefault("count", 0);
                            boolean targetPlayers = (boolean) payload.getOrDefault("targetPlayers", false);
                            
                            for (int i = 0; i < count; i++) {
                                org.bukkit.entity.Entity entity = loc.getWorld().spawnEntity(loc.clone().add(Math.random() * 2 - 1, 0, Math.random() * 2 - 1), type);
                                
                                if (targetPlayers && entity instanceof Mob mob) {
                                    loc.getWorld().getPlayers().stream()
                                            .min(Comparator.comparingDouble(p -> p.getLocation().distanceSquared(loc)))
                                            .ifPresent(mob::setTarget);
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Ignore invalid payload
                    }
                }
            }
        });

        register("structure", new BlockBehaviorStrategy() {
            @Override
            public void onTrigger(dev.rono.igniscore.model.BlockInstance instance, Object context) {
                // Placeholder for future structure placement logic
            }
        });

        register("effect", new BlockBehaviorStrategy() {
            @Override
            public void onTrigger(dev.rono.igniscore.model.BlockInstance instance, Object context) {
                // Placeholder for future potion effect/particle logic
            }
        });
    }

    private static double getCustomDouble(BlockDefinition def, String key, double defaultValue) {
        Object val = def.getCustomData().get(key);
        if (val instanceof Number n) return n.doubleValue();
        return defaultValue;
    }

    private static boolean getCustomBoolean(BlockDefinition def, String key, boolean defaultValue) {
        Object val = def.getCustomData().get(key);
        if (val instanceof Boolean b) return b;
        return defaultValue;
    }
}
