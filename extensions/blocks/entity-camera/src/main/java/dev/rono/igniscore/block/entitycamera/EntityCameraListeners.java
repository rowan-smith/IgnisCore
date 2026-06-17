package dev.rono.igniscore.block.entitycamera;

import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class EntityCameraListeners implements OnBlockInteractListener {
    private final IgnisStrategyContext context;

    EntityCameraListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    void onPlacedInteract(BlockDefinition definition,
                          IgnisLocation location,
                          IgnisPlayer player,
                          dev.rono.igniscore.api.port.IgnisInteraction interaction,
                          IgnisItem heldItem,
                          CustomBlockAction action) {
        if (action != CustomBlockAction.OPEN) {
            return;
        }
        IgnisWorld world = worldAt(location);
        IgnisLocation center = Locations.toCenter(location);
        double radius = StrategySupport.customDouble(definition, "cameraRadius", 12.0);
        int duration = StrategySupport.customInt(definition, "cameraDurationTicks", 100);
        Object target = findNearestPassive(world, center, radius);
        if (target == null) {
            player.sendMessage("<red>No passive mob in range for camera link.</red>");
            return;
        }
        context.extensions().spectateEntity(player, target, duration);
        IgnisLocation entityLoc = world.getEntityLocation(target);
        if (entityLoc != null) {
            TheatricsSupport.scanBeam(world, center, entityLoc, "END_ROD");
        }
        world.playSound(center, "BLOCK_BEACON_POWER_SELECT", 0.7f, 1.4f);
        player.sendMessage("<light_purple>Entity camera linked for " + (duration / 20) + "s.</light_purple>");
    }

    private Object findNearestPassive(IgnisWorld world, IgnisLocation center, double radius) {
        Object nearest = null;
        double best = Double.MAX_VALUE;
        for (Object entity : world.getNearbyEntities(center, radius)) {
            if (!EntityUtilSupport.isPassive(entity)) {
                continue;
            }
            IgnisLocation loc = world.getEntityLocation(entity);
            if (loc == null) {
                continue;
            }
            double dx = loc.x() - center.x();
            double dy = loc.y() - center.y();
            double dz = loc.z() - center.z();
            double dist = dx * dx + dy * dy + dz * dz;
            if (dist < best) {
                best = dist;
                nearest = entity;
            }
        }
        return nearest;
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        onPlacedInteract(event.block().definition(), event.block().location(), event.player(), event.interaction(), event.heldItem(), event.action());
    }
}
