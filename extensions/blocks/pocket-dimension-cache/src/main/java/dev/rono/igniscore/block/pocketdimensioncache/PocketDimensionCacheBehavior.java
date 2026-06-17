package dev.rono.igniscore.block.pocketdimensioncache;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.util.Locations;
import dev.rono.igniscore.api.util.PlacedMetaSupport;

final class PocketDimensionCacheBehavior {
    private final IgnisStrategyContext context;

    PocketDimensionCacheBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onPlacedInteract(BlockDefinition definition,
                          IgnisLocation location,
                          IgnisPlayer player,
                          dev.rono.igniscore.api.port.IgnisInteraction interaction,
                          IgnisItem heldItem,
                          CustomBlockAction action) {
        if (action != CustomBlockAction.OPEN) {
            return;
        }
        IgnisWorld world = worldAt(location);
        IgnisLocation center = Locations.toCenter(location);
        player.sendMessage("<gray>Pocket cache opened — collecting nearby drops.</gray>");
         double radius = StrategySupport.customDouble(definition, "cacheRadius", 5.0);
         EntityUtilSupport.pullLoot(world, center, radius, 0.4);
         TheatricsSupport.sparkle(world, center, "ITEM_PICKUP", 12);
         world.playSound(center, "ENTITY_ITEM_PICKUP", 0.8f, 1.1f);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.getExtensionSupport().resolveWorld(location);
    }
}
