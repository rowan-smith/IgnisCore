package dev.rono.igniscore.item.quantumcoin;

import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.ItemClickEvent;
import dev.rono.igniscore.api.event.OnItemClickListener;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class QuantumCoinListeners implements OnItemClickListener {
    private final IgnisStrategyContext context;
    private final IgnisNbtService nbtService;

    QuantumCoinListeners(IgnisStrategyContext context) {
        this.context = context;
        this.nbtService = context.nbt();
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("use".equals(event.actionToken())) {
                boolean heads = Math.random() < 0.5;
                String result = heads ? "heads" : "tails";
                if (nbtService != null) {
                    nbtService.setItemString(event.item(), "ignis:coin_flip", result);
                }
                IgnisWorld world = event.player().getWorld();
                TheatricsSupport.sparkle(world, event.player().getLocation(), heads ? "VILLAGER_HAPPY" : "SMOKE", 6);
                world.playSound(event.player().getLocation(), "ENTITY_EXPERIENCE_ORB_PICKUP", 0.8f, heads ? 1.4f : 0.8f);
                event.player().sendMessage("<gold>Coin flip:</gold> <white>" + result + "</white>");
            }
    }
}
