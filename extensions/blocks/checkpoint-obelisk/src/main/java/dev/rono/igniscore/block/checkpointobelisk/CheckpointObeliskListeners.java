package dev.rono.igniscore.block.checkpointobelisk;

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
import dev.rono.igniscore.api.util.PlacedMetaSupport;

final class CheckpointObeliskListeners implements OnBlockInteractListener {
    private final IgnisStrategyContext context;

    CheckpointObeliskListeners(IgnisStrategyContext context) {
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
        nbtCheckpoint(player, center);
         player.sendMessage("<gold>Checkpoint recorded.</gold>");
         TheatricsSupport.pulseRing(world, center, 2.0, "TOTEM_OF_UNDYING");
         world.playSound(center, "UI_TOAST_CHALLENGE_COMPLETE", 0.7f, 1.0f);
    }

    private void nbtCheckpoint(IgnisPlayer player, IgnisLocation center) {
        player.sendActionBar("<gray>" + (int) center.x() + " " + (int) center.y() + " " + (int) center.z() + "</gray>");
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        onPlacedInteract(event.block().definition(), event.block().location(), event.player(), event.interaction(), event.heldItem(), event.action());
    }
}
