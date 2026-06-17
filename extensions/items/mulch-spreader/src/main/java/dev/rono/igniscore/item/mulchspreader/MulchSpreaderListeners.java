package dev.rono.igniscore.item.mulchspreader;

import dev.rono.extensions.shared.strategy.BlockScanSupport;
import dev.rono.igniscore.api.event.ItemClickEvent;
import dev.rono.igniscore.api.event.OnItemClickListener;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class MulchSpreaderListeners implements OnItemClickListener {
    private final IgnisStrategyContext context;

    MulchSpreaderListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("use".equals(event.actionToken())) {
                IgnisWorld world = event.player().getWorld();
                IgnisLocation below = event.player().getLocation().add(0, -1, 0);
                String material = world.getBlockMaterialKey(below).toLowerCase();
                if (material.contains("dirt_path") || material.contains("path")) {
                    world.setBlockMaterialKey(below, "moss_block");
                    world.spawnParticle(below.add(0.5, 1, 0.5), "SPORE_BLOSSOM_AIR", 4, 0.2, 0.1, 0.2, 0.01);
                    world.playSound(below, "BLOCK_MOSS_PLACE", 0.6f, 1.0f);
                } else {
                    BlockScanSupport.mossifyNearWater(world, below, 2);
                }
                event.item().setAmount(Math.max(0, event.item().getAmount() - 1));
            }
    }
}
