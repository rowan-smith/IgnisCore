package dev.rono.igniscore.item.cableties;

import dev.rono.extensions.shared.strategy.BlockLinkSupport;
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

final class CableTiesListeners implements OnItemClickListener {
    private final IgnisStrategyContext context;
    private final IgnisNbtService nbt;

    CableTiesListeners(IgnisStrategyContext context) {
        this.context = context;
        this.nbt = context.nbt();
    }

    private void drawCable(IgnisWorld world, IgnisLocation from, IgnisLocation to) {
        int steps = 12;
        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            IgnisLocation p = new IgnisLocation(from.worldName(),
                    from.x() + (to.x() - from.x()) * t,
                    from.y() + (to.y() - from.y()) * t,
                    from.z() + (to.z() - from.z()) * t);
            world.spawnParticle(p, "CRIT", 1, 0, 0, 0, 0);
        }
        world.playSound(from, "ENTITY_LEASH_KNOT_PLACE", 0.7f, 1.2f);
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("use".equals(event.actionToken())) {
                IgnisWorld world = event.player().getWorld();
                if (event.clickedBlock() == null) {
                    IgnisLocation a = BlockLinkSupport.readLocation(nbt, event.item());
                    if (a == null) {
                        event.player().sendMessage("<gray>Click a fence post to start a cable.</gray>");
                        return;
                    }
                    event.player().sendMessage("<gray>Cable anchored — click second post.</gray>");
                    BlockLinkSupport.clear(nbt, event.item());
                    return;
                }
                String mat = event.clickedBlock().getMaterialKey().toLowerCase();
                if (!mat.contains("fence")) {
                    event.player().sendMessage("<gray>Cable ties only work on fence posts.</gray>");
                    return;
                }
                if (!BlockLinkSupport.hasLink(nbt, event.item())) {
                    BlockLinkSupport.link(nbt, event.item(), "fence", event.clickedBlock().getLocation());
                    event.player().sendMessage("<aqua>First post marked.</aqua>");
                    return;
                }
                IgnisLocation start = BlockLinkSupport.readLocation(nbt, event.item());
                IgnisLocation end = event.clickedBlock().getLocation();
                if (start != null) {
                    drawCable(world, start.add(0.5, 0.5, 0.5), end.add(0.5, 1.0, 0.5));
                }
                BlockLinkSupport.clear(nbt, event.item());
                event.item().setAmount(event.item().getAmount() - 1);
                event.player().sendMessage("<gray>Cable tied between posts.</gray>");
            }
    }
}
