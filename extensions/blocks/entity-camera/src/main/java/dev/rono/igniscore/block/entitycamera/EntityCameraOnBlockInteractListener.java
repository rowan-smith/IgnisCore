package dev.rono.igniscore.block.entitycamera;

import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class EntityCameraOnBlockInteractListener implements OnBlockInteractListener {
    private final IgnisStrategyContext context;

    EntityCameraOnBlockInteractListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.action() != CustomBlockAction.OPEN) {
            return;
        }
        IgnisWorld world = EntityCameraSupport.worldAt(context, event.block().location());
        IgnisLocation center = Locations.toCenter(event.block().location());
        double radius = StrategySupport.customDouble(event.block().definition(), "cameraRadius", 12.0);
        int duration = StrategySupport.customInt(event.block().definition(), "cameraDurationTicks", 100);
        Object target = EntityCameraSupport.findNearestPassive(context, world, center, radius);
        if (target == null) {
            event.player().sendMessage("<red>No passive mob in range for camera link.</red>");
            return;
        }
        context.extensions().spectateEntity(event.player(), target, duration);
        IgnisLocation entityLoc = world.getEntityLocation(target);
        if (entityLoc != null) {
            TheatricsSupport.scanBeam(world, center, entityLoc, "END_ROD");
        }
        world.playSound(center, "BLOCK_BEACON_POWER_SELECT", 0.7f, 1.4f);
        event.player().sendMessage("<light_purple>Entity camera linked for " + (duration / 20) + "s.</light_purple>");
    }
}

