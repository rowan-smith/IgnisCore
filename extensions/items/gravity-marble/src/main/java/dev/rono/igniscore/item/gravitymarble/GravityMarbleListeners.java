package dev.rono.igniscore.item.gravitymarble;

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
import dev.rono.igniscore.api.strategy.StrategySupport;

final class GravityMarbleListeners implements OnItemClickListener {
    private final IgnisStrategyContext context;

    GravityMarbleListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item, IgnisBlock clickedBlock) {
        IgnisWorld world = player.getWorld();
        IgnisLocation eye = player.getEyeLocation();
        double speed = StrategySupport.customDouble(definition.getCustomData(), "marbleSpeed", 1.2);
        Object marble = world.spawnProjectile("snowball", eye, player, 0, 0, speed);
        if (marble != null) {
            double yaw = Math.toRadians(eye.yaw());
            double pitch = Math.toRadians(eye.pitch());
            world.setEntityVelocity(marble,
                    -Math.sin(yaw) * Math.cos(pitch) * speed,
                    -Math.sin(pitch) * speed,
                    Math.cos(yaw) * Math.cos(pitch) * speed);
        }
        TheatricsSupport.sparkle(world, eye, "END_ROD", 4);
        world.playSound(eye, "ENTITY_SLIME_JUMP", 0.7f, 1.6f);
        item.setAmount(item.getAmount() - 1);
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("use".equals(event.actionToken())) {
                onItemUse(event.player(), event.definition(), event.item(), event.clickedBlock());
            }
    }
}
