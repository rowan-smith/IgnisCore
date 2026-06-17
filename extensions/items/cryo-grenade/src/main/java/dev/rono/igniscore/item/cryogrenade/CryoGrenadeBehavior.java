package dev.rono.igniscore.item.cryogrenade;

import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.EntityPhysicsSupport;
import dev.rono.extensions.shared.strategy.ThrowableSupport;

final class CryoGrenadeBehavior {
    private final IgnisStrategyContext context;

    CryoGrenadeBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item) {
        ThrowableSupport.throwProjectile(context, player, definition, item, (world, impact) -> {
            double radius = StrategySupport.customDouble(definition.getCustomData(), "freezeRadius", 6.0);
            int duration = StrategySupport.customInt(definition.getCustomData(), "freezeDuration", 40);
            world.playSound(impact, "BLOCK_GLASS_BREAK", 1.2f, 0.5f);
            world.spawnParticle(impact, "SNOWFLAKE", 30, radius * 0.3, 0.3, radius * 0.3, 0.05);
            EntityPhysicsSupport.freezeEntities(world, impact, radius, duration);
        });
    }
}
