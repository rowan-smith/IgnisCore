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

    @Override
    public void onItemClick(ItemClickEvent event) {
        if ("use".equals(event.actionToken())) {
                IgnisWorld world = event.player().getWorld();
                IgnisLocation eye = event.player().getEyeLocation();
                double speed = StrategySupport.customDouble(event.definition().getCustomData(), "marbleSpeed", 1.2);
                Object marble = world.spawnProjectile("snowball", eye, event.player(), 0, 0, speed);
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
                event.item().setAmount(event.item().getAmount() - 1);
            }
    }
}
