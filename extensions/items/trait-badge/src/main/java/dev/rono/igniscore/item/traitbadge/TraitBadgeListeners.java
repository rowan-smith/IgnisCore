package dev.rono.igniscore.item.traitbadge;

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

final class TraitBadgeListeners implements OnItemClickListener {
    private final IgnisStrategyContext context;
    private final IgnisNbtService nbtService;

    TraitBadgeListeners(IgnisStrategyContext context) {
        this.context = context;
        this.nbtService = context.nbt();
    }

    private String pickTrait(int roll) {
        String[] traits = {"Artisan", "Scout", "Alchemist", "Engineer", "Duelist"};
        return traits[Math.floorMod(roll, traits.length)];
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("use".equals(event.actionToken())) {
                IgnisWorld world = event.player().getWorld();
                IgnisLocation loc = event.player().getEyeLocation();
                int roll = nbtService.getItemInt(event.item(), "ignis:trait_roll", 0) + 1;
                nbtService.setItemInt(event.item(), "ignis:trait_roll", roll);
                String trait = StrategySupport.customBoolean(event.definition().getCustomData(), "randomTrait", true)
                        ? pickTrait(roll)
                        : "Artisan";
                nbtService.setItemString(event.item(), "ignis:trait", trait);
                event.player().sendMessage("<light_purple>Badge trait: <white>" + trait + "</white></light_purple>");
                TheatricsSupport.sparkle(world, loc, "TOTEM_OF_UNDYING", 10);
                world.playSound(loc, "ENTITY_PLAYER_LEVELUP", 0.6f, 1.4f);
            }
    }
}
