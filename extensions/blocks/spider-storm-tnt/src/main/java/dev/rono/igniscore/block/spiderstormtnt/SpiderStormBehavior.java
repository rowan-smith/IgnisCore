package dev.rono.igniscore.block.spiderstormtnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

import java.util.Comparator;
import java.util.Map;

final class SpiderStormBehavior {
    private final IgnisStrategyContext context;
    private final IgnisNbtService nbtService;

    SpiderStormBehavior(IgnisStrategyContext context) {
        this.context = context;
        this.nbtService = context.getNbtService();
    }

    void onPlaced(IgnisLocation location) {
        IgnisLocation center = Locations.toCenter(location);
        IgnisWorld world = worldAt(center);
        world.spawnParticle(center, "SPORE_BLOSSOM_AIR", 18, 0.45, 0.45, 0.45, 0.01);
        world.spawnParticle(center, "SMOKE", 8, 0.3, 0.3, 0.3, 0.01);
    }

    void onTrigger(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        float finalPower = StrategySupport.resolvePower(def, 4.0);
        boolean realExplosion = StrategySupport.customBoolean(def, "realExplosion", true);

        if (realExplosion) {
            world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.0f, 1.0f);
            StrategySupport.createExplosion(world, loc, def, 4.0, false);
        } else {
            spawnBurst(world, loc, finalPower);
        }

        spawnEntityPayload(world, def, loc, finalPower);
    }

    private void spawnEntityPayload(IgnisWorld world, BlockDefinition def, IgnisLocation loc, float finalPower) {
        Object payloadObj = def.getCustomData().get("entityPayload");
        if (!(payloadObj instanceof Map<?, ?> payload)) {
            return;
        }

        try {
            Object typeObj = payload.get("type");
            String typeStr = typeObj != null ? typeObj.toString() : null;
            if (typeStr == null) {
                return;
            }

            int count = payload.get("count") instanceof Number number ? number.intValue() : 0;
            boolean targetPlayers = payload.get("targetPlayers") instanceof Boolean enabled && enabled;

            for (int i = 0; i < count; i++) {
                final int index = i;
                double angle = Math.random() * Math.PI * 2.0;
                double distance = Math.random() * Math.max(2.0, finalPower * 0.65);
                IgnisLocation spawnLoc = loc.add(Math.cos(angle) * distance, 0, Math.sin(angle) * distance);
                Object entity = world.spawnEntity(typeStr, spawnLoc);
                double launchStrength = 0.35 + Math.random() * 0.25;
                world.setEntityVelocity(
                        entity,
                        Math.cos(angle) * launchStrength,
                        0.45 + Math.random() * 0.25,
                        Math.sin(angle) * launchStrength);

                nbtService.setEntityString(entity, "ignis:origin_block", def.getId());
                nbtService.setEntityString(entity, "ignis:spawn_index", Integer.toString(index));
                nbtService.setEntityString(entity, "ignis:is_custom_mob", "true");

                if ("SPIDER".equalsIgnoreCase(typeStr)) {
                    nbtService.setEntityString(entity, "ignis:spider_count", Integer.toString(count));
                    nbtService.setEntityString(entity, "ignis:aggression_radius", Double.toString(finalPower));
                }

                if (targetPlayers) {
                    world.getPlayersNear(loc, finalPower * 2).stream()
                            .min(Comparator.comparingDouble(player -> distanceSquared(player.getLocation(), loc)))
                            .ifPresent(target -> world.setEntityTarget(entity, target));
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void spawnBurst(IgnisWorld world, IgnisLocation center, float power) {
        double spread = Math.max(3.0, power * 0.45);
        world.playSound(center, "ENTITY_GENERIC_EXPLODE", 2.0f, 1.25f);
        world.playSound(center, "ENTITY_SPIDER_AMBIENT", 3.0f, 0.65f);
        world.spawnParticle(center, "EXPLOSION_EMITTER", 3, 1.0, 0.5, 1.0, 0.0);
        world.spawnParticle(center, "SMOKE", 160, spread, 1.4, spread, 0.04);
        world.spawnParticle(center, "CLOUD", 120, spread * 0.8, 1.1, spread * 0.8, 0.08);
        world.spawnParticle(center, "BLOCK", 90, spread * 0.5, 0.8, spread * 0.5, 0.02);
    }

    private double distanceSquared(IgnisLocation a, IgnisLocation b) {
        double dx = a.x() - b.x();
        double dy = a.y() - b.y();
        double dz = a.z() - b.z();
        return dx * dx + dy * dy + dz * dz;
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.getExtensionSupport().resolveWorld(location);
    }
}
