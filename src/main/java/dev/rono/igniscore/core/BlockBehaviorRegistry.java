package dev.rono.igniscore.core;

import dev.rono.igniscore.api.IgnisCoreAPI;
import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.RuntimeBlockInstance;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;

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

    public static void init() {
        // Explosion Strategy Implementation
        BlockBehaviorStrategy explosionBase = new BlockBehaviorStrategy() {
            @Override
            public void onTrigger(RuntimeBlockInstance instance, Object context) {
                BlockDefinition def = instance.getDefinition();
                org.bukkit.Location loc = instance.getLocation();
                float power = (float) (getCustomDouble(def, "power", 4.0) * getCustomDouble(def, "multiplier", 1.0));
                
                // Store runtime state in NBT for potential use by other systems
                instance.getData().setFloat("ignis:blast_power", power);
                
                loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                loc.getWorld().createExplosion(loc, power, getCustomBoolean(def, "fire", false), getCustomBoolean(def, "blockDamage", true));
            }
        };

        register("default", explosionBase);

        register("nuclear", new BlockBehaviorStrategy() {
            @Override
            public void onPlace(RuntimeBlockInstance instance) {
                Location center = instance.getLocation().toCenterLocation();
                center.getWorld().playSound(center, Sound.BLOCK_BEACON_ACTIVATE, 2.0f, 0.6f);
            }

            @Override
            public void onTick(RuntimeBlockInstance instance) {
                playNukeCountdown(instance);
                spawnNukeFuseParticles(instance);
            }

            @Override
            public void onTrigger(RuntimeBlockInstance instance, Object context) {
                BlockDefinition def = instance.getDefinition();
                org.bukkit.Location loc = instance.getLocation().toCenterLocation();
                double basePower = def.getRadius() > 0 ? def.getRadius() : getCustomDouble(def, "power", 10.0);
                float finalPower = (float) (basePower * getCustomDouble(def, "multiplier", 1.0));
                
                // structured NBT metadata for the nuke event
                instance.getData().setFloat("ignis:nuke_power", finalPower);
                instance.getData().setDouble("ignis:radiation_radius", finalPower * 2.0);
                
                spawnNukeDetonationParticles(loc, finalPower);
                loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 8.0f, 0.45f);
                loc.getWorld().playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 8.0f, 0.55f);
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
            public void onTrigger(RuntimeBlockInstance instance, Object context) {
                BlockDefinition def = instance.getDefinition();
                org.bukkit.Location loc = instance.getLocation().toCenterLocation();
                double basePower = def.getRadius() > 0 ? def.getRadius() : getCustomDouble(def, "power", 4.0);
                float finalPower = (float) (basePower * getCustomDouble(def, "multiplier", 1.0));
                boolean realExplosion = getCustomBoolean(def, "realExplosion", true);

                if (realExplosion) {
                    loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                    loc.getWorld().createExplosion(loc, finalPower, getCustomBoolean(def, "fire", false), getCustomBoolean(def, "blockDamage", true));
                } else {
                    spawnSpiderStormBurst(loc, finalPower);
                }
                
                Object payloadObj = def.getCustomData().get("entityPayload");
                if (payloadObj instanceof Map<?, ?> payload) {
                    try {
                        Object typeObj = payload.get("type");
                        String typeStr = typeObj != null ? typeObj.toString() : null;
                        if (typeStr != null) {
                            EntityType type = EntityType.valueOf(typeStr.toUpperCase());
                            int count = payload.get("count") instanceof Number n ? n.intValue() : 0;
                            boolean targetPlayers = payload.get("targetPlayers") instanceof Boolean b ? b : false;
                            
                            for (int i = 0; i < count; i++) {
                                final int index = i;
                                double angle = Math.random() * Math.PI * 2.0;
                                double distance = Math.random() * Math.max(2.0, finalPower * 0.65);
                                org.bukkit.entity.Entity entity = loc.getWorld().spawnEntity(loc.clone().add(Math.cos(angle) * distance, 0, Math.sin(angle) * distance), type);
                                double launchStrength = 0.35 + Math.random() * 0.25;
                                entity.setVelocity(new Vector(
                                        Math.cos(angle) * launchStrength,
                                        0.45 + Math.random() * 0.25,
                                        Math.sin(angle) * launchStrength
                                ));
                                
                                // STRUCTURED ENTITY METADATA using NBT-API
                                IgnisCoreAPI.getNbtService().editEntity(entity, nbt -> {
                                    nbt.setString("ignis:origin_block", def.getId());
                                    nbt.setInteger("ignis:spawn_index", index);
                                    nbt.setBoolean("ignis:is_custom_mob", true);
                                    
                                    // Spider Storm specific metadata example
                                    if (type == EntityType.SPIDER) {
                                        nbt.setInteger("ignis:spider_count", count);
                                        nbt.setDouble("ignis:aggression_radius", (double) finalPower);
                                    }
                                });

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
            public void onTrigger(RuntimeBlockInstance instance, Object context) {
                // Placeholder for future structure placement logic
            }
        });

        register("effect", new BlockBehaviorStrategy() {
            @Override
            public void onTrigger(RuntimeBlockInstance instance, Object context) {
                // Placeholder for future potion effect/particle logic
            }
        });
    }

    private static void playNukeCountdown(RuntimeBlockInstance instance) {
        int ticksLeft = instance.getTicksLeft();
        int fuse = Math.max(1, instance.getDefinition().getFuse());
        int elapsed = Math.max(0, fuse - ticksLeft);
        int interval = ticksLeft > 80 ? 20 : ticksLeft > 40 ? 10 : ticksLeft > 15 ? 5 : 2;
        if (elapsed % interval != 0) return;

        Location center = instance.getLocation().toCenterLocation();
        float pitch = ticksLeft <= 15 ? 1.9f : ticksLeft <= 40 ? 1.45f : ticksLeft <= 80 ? 1.1f : 0.75f;
        center.getWorld().playSound(center, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f, pitch);
        center.getWorld().playSound(center, Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.5f);
    }

    private static void spawnNukeFuseParticles(RuntimeBlockInstance instance) {
        int ticksLeft = instance.getTicksLeft();
        int interval = ticksLeft > 40 ? 10 : 4;
        if (ticksLeft % interval != 0) return;

        Location center = instance.getLocation().toCenterLocation();
        World world = center.getWorld();
        double intensity = ticksLeft <= 20 ? 1.0 : ticksLeft <= 60 ? 0.6 : 0.3;
        world.spawnParticle(Particle.SMOKE, center, (int) (18 * intensity), 0.45, 0.45, 0.45, 0.02);
        world.spawnParticle(Particle.FLAME, center, (int) (10 * intensity), 0.35, 0.35, 0.35, 0.04);
        world.spawnParticle(Particle.LAVA, center, (int) (4 * intensity), 0.25, 0.25, 0.25, 0.0);
    }

    private static void spawnNukeDetonationParticles(Location center, float power) {
        World world = center.getWorld();
        double spread = Math.max(8.0, power * 0.8);
        world.spawnParticle(Particle.FLASH, center, 1, 0, 0, 0, 0, Color.WHITE);
        world.spawnParticle(Particle.EXPLOSION_EMITTER, center, Math.max(12, (int) (power * 0.6)), spread * 0.35, spread * 0.2, spread * 0.35, 0.0);
        world.spawnParticle(Particle.FLAME, center, Math.max(300, (int) (power * 12)), spread, spread * 0.55, spread, 0.12);
        world.spawnParticle(Particle.SMOKE, center.clone().add(0, power * 0.5, 0), Math.max(450, (int) (power * 16)), spread * 0.8, spread * 0.75, spread * 0.8, 0.05);
        world.spawnParticle(Particle.CLOUD, center.clone().add(0, power * 0.35, 0), Math.max(300, (int) (power * 10)), spread * 0.7, spread * 0.55, spread * 0.7, 0.08);
        world.spawnParticle(Particle.LAVA, center, Math.max(80, (int) (power * 3)), spread * 0.45, spread * 0.25, spread * 0.45, 0.0);
    }

    private static void spawnSpiderStormBurst(Location center, float power) {
        World world = center.getWorld();
        double spread = Math.max(3.0, power * 0.45);
        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 1.25f);
        world.playSound(center, Sound.ENTITY_SPIDER_AMBIENT, 3.0f, 0.65f);
        world.spawnParticle(Particle.EXPLOSION_EMITTER, center, 3, 1.0, 0.5, 1.0, 0.0);
        world.spawnParticle(Particle.SMOKE, center, 160, spread, 1.4, spread, 0.04);
        world.spawnParticle(Particle.CLOUD, center, 120, spread * 0.8, 1.1, spread * 0.8, 0.08);
        world.spawnParticle(Particle.BLOCK, center, 90, spread * 0.5, 0.8, spread * 0.5, 0.02, org.bukkit.Material.COBWEB.createBlockData());
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
