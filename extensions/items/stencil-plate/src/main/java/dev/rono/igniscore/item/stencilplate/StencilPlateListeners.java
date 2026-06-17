package dev.rono.igniscore.item.stencilplate;

import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.ItemClickEvent;
import dev.rono.igniscore.api.event.OnItemClickListener;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class StencilPlateListeners implements OnItemClickListener {
    private final IgnisStrategyContext context;

    StencilPlateListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("use".equals(event.actionToken())) {
                if (event.clickedBlock() == null) {
                    return;
                }
                IgnisLocation loc = event.clickedBlock().getLocation();
                IgnisWorld world = event.player().getWorld();
                String material = world.getBlockMaterialKey(loc).toLowerCase();
                if (!material.contains("concrete_powder")) {
                    event.player().sendMessage("<gray>Stencil only works on concrete powder.</gray>");
                    return;
                }
                for (int x = -1; x <= 1; x++) {
                    for (int y = 0; y <= 1; y++) {
                        world.spawnParticle(loc.add(0.5 + x * 0.3, 0.5 + y * 0.3, 0.5), "GLOW", 2, 0.05, 0.05, 0.05, 0.01);
                    }
                }
                world.playSound(loc, "BLOCK_SAND_PLACE", 0.8f, 0.9f);
                event.player().sendMessage("<aqua>Stencil pattern applied before hardening.</aqua>");
                event.item().setAmount(event.item().getAmount() - 1);
            }
    }
}
