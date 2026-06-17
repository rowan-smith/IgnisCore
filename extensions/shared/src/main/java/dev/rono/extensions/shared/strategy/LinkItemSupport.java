package dev.rono.extensions.shared.strategy;

import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;

/**
 * Shared logic for items that link to and remotely activate blocks.
 */
public final class LinkItemSupport {
    private LinkItemSupport() {
    }

    public static void onUse(IgnisStrategyContext context,
                             IgnisPlayer player,
                             ItemDefinition definition,
                             IgnisItem item,
                             IgnisBlock clickedBlock,
                             String expectedBlockType,
                             String remoteAction,
                             double linkRange) {
        IgnisNbtService nbt = context.nbt();
        IgnisWorld world = player.getWorld();

        if (clickedBlock != null) {
            BlockLinkSupport.link(nbt, item, expectedBlockType, clickedBlock.getLocation());
            player.sendMessage("<aqua>Linked to block at "
                    + (int) clickedBlock.getLocation().x() + " "
                    + (int) clickedBlock.getLocation().y() + " "
                    + (int) clickedBlock.getLocation().z() + "</aqua>");
            TheatricsSupport.sparkle(world, clickedBlock.getLocation(), "END_ROD", 6);
            world.playSound(clickedBlock.getLocation(), "BLOCK_BEACON_ACTIVATE", 0.7f, 1.2f);
            return;
        }

        IgnisLocation target = BlockLinkSupport.readLocation(nbt, item);
        if (target == null || !expectedBlockType.equals(BlockLinkSupport.readType(nbt, item))) {
            player.sendMessage("<red>Right-click a block to link first.</red>");
            return;
        }
        double range = StrategySupport.customDouble(definition.getCustomData(), "linkRange", linkRange);
        if (!BlockLinkSupport.withinRange(player, target, range)) {
            player.sendMessage("<red>Linked block out of range (" + (int) range + "m).</red>");
            return;
        }
        if (!LinkedBlockRegistry.activate(target, player, remoteAction)) {
            player.sendMessage("<red>Linked block is not active.</red>");
            return;
        }
        TheatricsSupport.pulseRing(world, target, 1.5, "END_ROD");
        world.playSound(target, "BLOCK_BEACON_POWER_SELECT", 0.8f, 1.0f);
    }
}
