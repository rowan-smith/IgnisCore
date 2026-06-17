package dev.rono.igniscore.item.orexraygoggles;

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

final class OreXrayGogglesListeners implements OnItemClickListener {
    private final IgnisStrategyContext context;
    private final IgnisNbtService nbtService;

    OreXrayGogglesListeners(IgnisStrategyContext context) {
        this.context = context;
        this.nbtService = context.nbt();
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("use".equals(event.actionToken())) {
                IgnisWorld world = event.player().getWorld();
                IgnisLocation loc = event.player().getEyeLocation();
                int radius = StrategySupport.customInt(event.definition().getCustomData(), "xrayRadius", 14);
                IgnisLocation ore = BlockScanSupport.findNearestOre(world, loc, radius);
                if (ore == null) {
                    event.player().sendMessage("<gray>No ore signature detected.</gray>");
                    return;
                }
                event.player().sendMessage("<green>Ore ping toward <white>" + (int) ore.x() + " " + (int) ore.y() + " " + (int) ore.z() + "</white></green>");
                TheatricsSupport.scanBeam(world, loc, ore.add(0.5, 0.5, 0.5), "CRIT");
                world.playSound(loc, "BLOCK_AMETHYST_BLOCK_RESONATE", 0.7f, 1.5f);
            }
    }
}
