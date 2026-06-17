package dev.rono.igniscore.block.waypointmarker;

import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;
import dev.rono.igniscore.api.util.PlacedMetaSupport;

final class WaypointMarkerOnBlockInteractListener implements OnBlockInteractListener {
    private final IgnisStrategyContext context;

    WaypointMarkerOnBlockInteractListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.action() != CustomBlockAction.OPEN) {
            return;
        }
        IgnisWorld world = WaypointMarkerSupport.worldAt(context, event.block().location());
        IgnisLocation center = Locations.toCenter(event.block().location());
        String name = StrategySupport.customBoolean(event.block().definition(), "usePlayerName", true)
                ? event.player().getName()
                : StrategySupport.customInt(event.block().definition(), "waypointId", 1) + "";
         PlacedMetaSupport.setString(event.block().location(), name + ":" + center.x() + "," + center.y() + "," + center.z());
         event.player().sendMessage("<aqua>Waypoint <white>" + name + "</white> saved.</aqua>");
         TheatricsSupport.sparkle(world, center, "END_ROD", 10);
         world.playSound(center, "ENTITY_EXPERIENCE_ORB_PICKUP", 0.8f, 1.2f);
    }
}

