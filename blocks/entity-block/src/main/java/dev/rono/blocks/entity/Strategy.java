package dev.rono.blocks.entity;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;

import java.util.Comparator;
import java.util.Map;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder()
                .placementSound("ENTITY_SPIDER_AMBIENT")
                .build();
    }

    @Override
    public void onStaticPlace(BlockDefinition definition, Location location) {
        if (!"spider-storm".equalsIgnoreCase(definition.getId())) {
            return;
        }
        Location center = location.toCenterLocation();
        StrategySupport.spawnParticles(center, Particle.SPORE_BLOSSOM_AIR, 18, 0.45, 0.45, 0.45, 0.01);
        StrategySupport.spawnParticles(center, Particle.SMOKE, 8, 0.3, 0.3, 0.3, 0.01);
    }

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        BlockDefinition def = instance.getDefinition();
        Location loc = instance.getLocation().toCenterLocation();
        float finalPower = StrategySupport.resolvePower(def, 4.0);
        boolean realExplosion = StrategySupport.customBoolean(def, "realExplosion", true);

        if (realExplosion) {
            loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
            StrategySupport.createExplosion(loc, def, 4.0, false);
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
                        org.bukkit.entity.Entity entity = loc.getWorld().spawnEntity(
                                loc.clone().add(Math.cos(angle) * distance, 0, Math.sin(angle) * distance), type);
                        double launchStrength = 0.35 + Math.random() * 0.25;
                        entity.setVelocity(new Vector(
                                Math.cos(angle) * launchStrength,
                                0.45 + Math.random() * 0.25,
                                Math.sin(angle) * launchStrength
                        ));

                        Strategy.this.context.getNbtService().editEntity(entity, nbt -> {
                            nbt.setString("ignis:origin_block", def.getId());
                            nbt.setInteger("ignis:spawn_index", index);
                            nbt.setBoolean("ignis:is_custom_mob", true);

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
            } catch (Exception ignored) {
            }
        }
    }

    private void spawnSpiderStormBurst(Location center, float power) {
        double spread = Math.max(3.0, power * 0.45);
        center.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 1.25f);
        center.getWorld().playSound(center, Sound.ENTITY_SPIDER_AMBIENT, 3.0f, 0.65f);
        StrategySupport.spawnParticles(center, Particle.EXPLOSION_EMITTER, 3, 1.0, 0.5, 1.0, 0.0);
        StrategySupport.spawnParticles(center, Particle.SMOKE, 160, spread, 1.4, spread, 0.04);
        StrategySupport.spawnParticles(center, Particle.CLOUD, 120, spread * 0.8, 1.1, spread * 0.8, 0.08);
        StrategySupport.spawnParticles(center, Particle.BLOCK, 90, spread * 0.5, 0.8, spread * 0.5, 0.02,
                Material.COBWEB.createBlockData());
    }
}
