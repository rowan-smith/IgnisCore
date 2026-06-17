package dev.rono.igniscore.block.ouijaslab;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class OuijaSlabSupport {
    private OuijaSlabSupport() {
    }

    static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    static void tick(IgnisStrategyContext ctx, BlockDefinition definition, IgnisLocation location) {

        IgnisWorld world = worldAt(ctx, location);
        IgnisLocation block = Locations.toBlock(location);
        IgnisLocation[] corners = {
                block.add(0, 0, 0), block.add(1, 0, 0), block.add(0, 0, 1), block.add(1, 0, 1)
        };
        int playersOnCorners = 0;
        for (IgnisLocation corner : corners) {
            for (IgnisPlayer player : world.getPlayersNear(corner.add(0.5, 0, 0.5), 0.8)) {
                playersOnCorners++;
            }
        }
        if (playersOnCorners < StrategySupport.customInt(definition, "minPlayers", 2)) {
            return;
        }
        char letter = LETTERS.charAt((int) (Math.random() * LETTERS.length()));
        IgnisLocation center = Locations.toCenter(location);
        world.spawnParticle(center.add(0, 0.5, 0), "SOUL_FIRE_FLAME", 8, 0.3, 0.2, 0.3, 0.02);
        world.playSound(center, "BLOCK_SOUL_SAND_STEP", 0.5f, 1.5f);
        for (IgnisPlayer player : world.getPlayersNear(center, 6.0)) {
            player.sendActionBar("<dark_purple>… " + letter + " …</dark_purple>");
        }
    
    }

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

