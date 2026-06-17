package dev.rono.igniscore.block.spiderstormtnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import java.util.Comparator;
import java.util.Map;

final class SpiderStormSupport {
    private SpiderStormSupport() {
    }

    static void spawnEntityPayload(SpiderStormRuntime runtime, IgnisWorld world, BlockDefinition def, IgnisLocation loc, float finalPower) {

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

                runtime.nbtService.setEntityString(entity, "ignis:origin_block", def.getId());
                runtime.nbtService.setEntityString(entity, "ignis:spawn_index", Integer.toString(index));
                runtime.nbtService.setEntityString(entity, "ignis:is_custom_mob", "true");

                if ("SPIDER".equalsIgnoreCase(typeStr)) {
                    runtime.nbtService.setEntityString(entity, "ignis:spider_count", Integer.toString(count));
                    runtime.nbtService.setEntityString(entity, "ignis:aggression_radius", Double.toString(finalPower));
                }

                if (targetPlayers) {
                    world.getPlayersNear(loc, finalPower * 2).stream()
                            .min(Comparator.comparingDouble(player -> distanceSquared(runtime, player.getLocation(), loc)))
                            .ifPresent(target -> world.setEntityTarget(entity, target));
                }
            }
        } catch (Exception ignored) {
        }
    
    }

    static void spawnBurst(SpiderStormRuntime runtime, IgnisWorld world, IgnisLocation center, float power) {

        double spread = Math.max(3.0, power * 0.45);
        world.playSound(center, "ENTITY_GENERIC_EXPLODE", 2.0f, 1.25f);
        world.playSound(center, "ENTITY_SPIDER_AMBIENT", 3.0f, 0.65f);
        world.spawnParticle(center, "EXPLOSION_EMITTER", 3, 1.0, 0.5, 1.0, 0.0);
        world.spawnParticle(center, "SMOKE", 160, spread, 1.4, spread, 0.04);
        world.spawnParticle(center, "CLOUD", 120, spread * 0.8, 1.1, spread * 0.8, 0.08);
        world.spawnParticle(center, "BLOCK", 90, spread * 0.5, 0.8, spread * 0.5, 0.02);
    
    }

    static double distanceSquared(SpiderStormRuntime runtime, IgnisLocation a, IgnisLocation b) {

        double dx = a.x() - b.x();
        double dy = a.y() - b.y();
        double dz = a.z() - b.z();
        return dx * dx + dy * dy + dz * dz;
    
    }

    static IgnisWorld worldAt(SpiderStormRuntime runtime, IgnisLocation location) {

        return runtime.context.extensions().resolveWorld(location);
    
    }
}

