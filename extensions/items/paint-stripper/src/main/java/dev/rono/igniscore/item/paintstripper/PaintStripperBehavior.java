package dev.rono.igniscore.item.paintstripper;

import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.extensions.shared.strategy.TheatricsSupport;

final class PaintStripperBehavior {
    private final IgnisStrategyContext context;

    PaintStripperBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item, IgnisBlock clickedBlock) {
        if (clickedBlock == null) {
            return;
        }
        IgnisLocation loc = clickedBlock.getLocation();
        IgnisWorld world = player.getWorld();
        String material = world.getBlockMaterialKey(loc).toLowerCase();
        if (material.contains("banner")) {
            world.setBlockMaterialKey(loc, "white_banner");
            player.sendMessage("<gray>Banner patterns stripped.</gray>");
        } else if (material.contains("wool") || material.contains("leather")) {
            world.setBlockMaterialKey(loc, material.replaceAll("_(blue|red|green|yellow|orange|pink|purple|cyan|lime|gray|black|white|brown|magenta|light_blue)", "_white"));
            player.sendMessage("<gray>Dye removed from block.</gray>");
        } else {
            player.sendMessage("<gray>No paint or trim detected on this block.</gray>");
            return;
        }
        TheatricsSupport.sparkle(world, loc.add(0.5, 0.5, 0.5), "CLOUD", 6);
        world.playSound(loc, "BLOCK_WOOL_BREAK", 0.7f, 1.0f);
        item.setAmount(item.getAmount() - 1);
    }
}
