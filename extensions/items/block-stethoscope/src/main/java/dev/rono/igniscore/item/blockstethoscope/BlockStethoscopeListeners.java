package dev.rono.igniscore.item.blockstethoscope;

import dev.rono.extensions.shared.strategy.BlockScanSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.ItemClickEvent;
import dev.rono.igniscore.api.event.OnItemClickListener;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class BlockStethoscopeListeners implements OnItemClickListener {
    private final IgnisStrategyContext context;
    private final IgnisNbtService nbtService;

    BlockStethoscopeListeners(IgnisStrategyContext context) {
        this.context = context;
        this.nbtService = context.nbt();
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("use".equals(event.actionToken())) {
                IgnisWorld world = event.player().getWorld();
                IgnisLocation loc = event.player().getEyeLocation();
                IgnisBlock target = event.clickedBlock();
                if (target == null) {
                    event.player().sendMessage("<yellow>Aim at a block to listen.</yellow>");
                    return;
                }
                IgnisLocation blockLoc = target.getLocation();
                String material = world.getBlockMaterialKey(blockLoc);
                event.player().sendMessage("<gray>Stethoscope: <white>" + material + "</white></gray>");
                TheatricsSupport.scanBeam(world, loc, Locations.toCenter(blockLoc), "NOTE");
                world.playSound(loc, "BLOCK_NOTE_BLOCK_HARP", 0.7f, 1.3f);
            }
    }
}
