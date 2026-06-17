package dev.rono.igniscore.block.ouijaslab;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class OuijaSlabListeners implements OnBlockPlaceListener, OnBlockBreakListener {
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private final IgnisStrategyContext context;

    OuijaSlabListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    private void tick(BlockDefinition definition, IgnisLocation location) {
        IgnisWorld world = worldAt(location);
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

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
                PlacedTickSupport.start(context, event.block().location(), StrategySupport.customInt(event.block().definition(), "tickPeriod", 30),
                        () -> tick(event.block().definition(), event.block().location()));
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
                PlacedTickSupport.stop(event.block().location());
    }
}
