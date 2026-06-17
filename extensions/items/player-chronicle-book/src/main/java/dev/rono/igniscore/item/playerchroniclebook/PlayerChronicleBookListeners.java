package dev.rono.igniscore.item.playerchroniclebook;

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

final class PlayerChronicleBookListeners implements OnItemClickListener {
    private final IgnisStrategyContext context;
    private final IgnisNbtService nbtService;

    PlayerChronicleBookListeners(IgnisStrategyContext context) {
        this.context = context;
        this.nbtService = context.nbt();
    }

    void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item, IgnisBlock clickedBlock) {
        IgnisWorld world = player.getWorld();
        IgnisLocation loc = player.getEyeLocation();
        int page = nbtService.getItemInt(item, "ignis:chronicle_page", 0) + 1;
        nbtService.setItemInt(item, "ignis:chronicle_page", page);
        String entry = player.getName() + " @ " + (int) loc.x() + "," + (int) loc.y() + "," + (int) loc.z();
        nbtService.setItemString(item, "ignis:chronicle_" + page, entry);
        player.sendMessage("<gold>Chronicle page <white>" + page + "</white> written.</gold>");
        world.playSound(loc, "ITEM_BOOK_PAGE_TURN", 0.8f, 1.0f);
        TheatricsSupport.sparkle(world, loc, "ENCHANT", 6);
    }


    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("use".equals(event.actionToken())) {
                onItemUse(event.player(), event.definition(), event.item(), event.clickedBlock());
            }
    }
}
