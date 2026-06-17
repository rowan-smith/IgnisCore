package dev.rono.igniscore.item.chunkgridoverlay;

import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.BlockScanSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.util.Locations;

final class ChunkGridOverlayBehavior {
    private final IgnisStrategyContext context;
    private final IgnisNbtService nbtService;

    ChunkGridOverlayBehavior(IgnisStrategyContext context) {
        this.context = context;
        this.nbtService = context.getNbtService();
    }

    void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item, IgnisBlock clickedBlock) {
        IgnisWorld world = player.getWorld();
        IgnisLocation loc = player.getEyeLocation();
        int chunkX = (int) Math.floor(loc.x()) >> 4;
        int chunkZ = (int) Math.floor(loc.z()) >> 4;
        nbtService.setItemString(item, "ignis:chunk", chunkX + "," + chunkZ);
        player.sendActionBar("<gray>Chunk " + chunkX + ", " + chunkZ + "</gray>");
        double size = 8.0;
        IgnisLocation corner = new IgnisLocation(loc.worldId(), loc.worldName(), chunkX * 16.0, loc.y(), chunkZ * 16.0, 0f, 0f);
        TheatricsSupport.pulseRing(world, corner.add(size, 0, size), size, "FLAME");
        world.playSound(loc, "BLOCK_BEACON_AMBIENT", 0.5f, 1.8f);
    }

}
