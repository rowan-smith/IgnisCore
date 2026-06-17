package dev.rono.igniscore.item.atlasimprinter;

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

final class AtlasImprinterListeners implements OnItemClickListener {
    private final IgnisStrategyContext context;
    private final IgnisNbtService nbtService;

    AtlasImprinterListeners(IgnisStrategyContext context) {
        this.context = context;
        this.nbtService = context.nbt();
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("use".equals(event.actionToken())) {
                IgnisWorld world = event.player().getWorld();
                IgnisLocation loc = event.player().getEyeLocation();
                int maps = nbtService.getItemInt(event.item(), "ignis:atlas_maps", 0) + 1;
                nbtService.setItemInt(event.item(), "ignis:atlas_maps", maps);
                String stamp = (int) loc.x() + ":" + (int) loc.z();
                nbtService.setItemString(event.item(), "ignis:atlas_" + maps, stamp);
                event.player().sendMessage("<gold>Atlas imprint <white>#" + maps + "</white> at " + stamp + "</gold>");
                TheatricsSupport.sparkle(world, loc, "COMPOSTER", 8);
                world.playSound(loc, "BLOCK_CARTOGRAPHY_TABLE_USE", 0.8f, 1.1f);
            }
    }
}
