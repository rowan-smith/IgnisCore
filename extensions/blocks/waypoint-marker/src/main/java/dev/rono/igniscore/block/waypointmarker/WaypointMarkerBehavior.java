package dev.rono.igniscore.block.waypointmarker;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.util.Locations;
import dev.rono.igniscore.api.util.PlacedMetaSupport;

final class WaypointMarkerBehavior {
    private final IgnisStrategyContext context;

    WaypointMarkerBehavior(IgnisStrategyContext context) {
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
        String name = StrategySupport.customBoolean(definition, "usePlayerName", true)
                ? player.getName()
                : StrategySupport.customInt(definition, "waypointId", 1) + "";
         PlacedMetaSupport.setString(location, name + ":" + center.x() + "," + center.y() + "," + center.z());
         player.sendMessage("<aqua>Waypoint <white>" + name + "</white> saved.</aqua>");
         TheatricsSupport.sparkle(world, center, "END_ROD", 10);
         world.playSound(center, "ENTITY_EXPERIENCE_ORB_PICKUP", 0.8f, 1.2f);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
