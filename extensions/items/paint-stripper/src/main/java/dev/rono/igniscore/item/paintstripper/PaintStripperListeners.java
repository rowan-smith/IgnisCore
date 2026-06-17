package dev.rono.igniscore.item.paintstripper;

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

final class PaintStripperListeners implements OnItemClickListener {
    private final IgnisStrategyContext context;

    PaintStripperListeners(IgnisStrategyContext context) {
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
                if (material.contains("banner")) {
                    world.setBlockMaterialKey(loc, "white_banner");
                    event.player().sendMessage("<gray>Banner patterns stripped.</gray>");
                } else if (material.contains("wool") || material.contains("leather")) {
                    world.setBlockMaterialKey(loc, material.replaceAll("_(blue|red|green|yellow|orange|pink|purple|cyan|lime|gray|black|white|brown|magenta|light_blue)", "_white"));
                    event.player().sendMessage("<gray>Dye removed from block.</gray>");
                } else {
                    event.player().sendMessage("<gray>No paint or trim detected on this block.</gray>");
                    return;
                }
                TheatricsSupport.sparkle(world, loc.add(0.5, 0.5, 0.5), "CLOUD", 6);
                world.playSound(loc, "BLOCK_WOOL_BREAK", 0.7f, 1.0f);
                event.item().setAmount(event.item().getAmount() - 1);
            }
    }
}
