package dev.rono.igniscore.block.checkpointobelisk;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class CheckpointObeliskSupport {
    private CheckpointObeliskSupport() {
    }

    static void nbtCheckpoint(IgnisStrategyContext ctx, IgnisPlayer player, IgnisLocation center) {

        player.sendActionBar("<gray>" + (int) center.x() + " " + (int) center.y() + " " + (int) center.z() + "</gray>");
    
    }

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

