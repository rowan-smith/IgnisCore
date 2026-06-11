package dev.rono.igniscore.strategies;

import dev.rono.igniscore.api.IgnisCoreAPI;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.core.BuiltinStrategyBootstrap;
import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.RuntimeBlockInstance;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;

import java.util.Comparator;
import java.util.Map;

public class EntityStrategy extends BaseBlockBehaviorStrategy {
    public EntityStrategy() {
        super(IgnisStrategyDescriptor.of("entity", "Entity Spawn", "1.0.0", "IgnisCore"));
    }

    @Override
    public dev.rono.igniscore.api.strategy.StrategyProfile profile(BlockDefinition definition) {
        return BuiltinStrategyBootstrap.explosiveProfile().toBuilder()
                .placementSound("ENTITY_SPIDER_AMBIENT")
                .build();
    }
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
}
