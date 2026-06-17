package dev.rono.igniscore.item.rivetgun;

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
import dev.rono.igniscore.api.strategy.StrategySupport;

final class RivetGunListeners implements OnItemClickListener {
    private final IgnisStrategyContext context;

    RivetGunListeners(IgnisStrategyContext context) {
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
                if (material.contains("reinforced") || material.contains("obsidian")) {
                    event.player().sendMessage("<gray>Block is already reinforced.</gray>");
                    return;
                }
                String reinforced = StrategySupport.customString(event.definition().getCustomData(), "reinforcedMaterial", "obsidian");
                world.setBlockMaterialKey(loc, reinforced);
                TheatricsSupport.sparkle(world, loc.add(0.5, 0.5, 0.5), "CRIT", 8);
                world.playSound(loc, "BLOCK_ANVIL_USE", 0.8f, 1.2f);
                event.player().sendMessage("<gray>Riveted block into reinforced variant.</gray>");
                event.item().setAmount(event.item().getAmount() - 1);
            }
    }
}
