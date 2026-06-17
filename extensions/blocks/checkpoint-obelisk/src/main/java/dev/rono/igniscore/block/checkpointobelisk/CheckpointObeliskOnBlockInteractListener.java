package dev.rono.igniscore.block.checkpointobelisk;

import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;

final class CheckpointObeliskOnBlockInteractListener implements OnBlockInteractListener {
    private final IgnisStrategyContext context;

    CheckpointObeliskOnBlockInteractListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.action() != CustomBlockAction.OPEN) {
            return;
        }
        IgnisWorld world = CheckpointObeliskSupport.worldAt(context, event.block().location());
        IgnisLocation center = Locations.toCenter(event.block().location());
        CheckpointObeliskSupport.nbtCheckpoint(context, event.player(), center);
         event.player().sendMessage("<gold>Checkpoint recorded.</gold>");
         TheatricsSupport.pulseRing(world, center, 2.0, "TOTEM_OF_UNDYING");
         world.playSound(center, "UI_TOAST_CHALLENGE_COMPLETE", 0.7f, 1.0f);
    }
}

