package dev.rono.igniscore.block.nuke;

import dev.rono.extensions.shared.config.ExplosionConfig;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;

final class NukeOnBlockTriggerListener implements OnBlockTriggerListener {
    private final IgnisStrategyContext context;

    NukeOnBlockTriggerListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        ExplosionConfig explosion = ExplosionConfig.from(event.definition());
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        float finalPower = explosion.resolvedPower();
        IgnisWorld world = NukeSupport.worldAt(context, loc);

        event.instance().getData().setDouble("ignis:nuke_power", finalPower);
        event.instance().getData().setDouble("ignis:radiation_radius", finalPower * 2.0);

        NukeSupport.spawnDetonationParticles(context, world, loc, finalPower);
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 8.0f, 0.45f);
        world.playSound(loc, "ENTITY_LIGHTNING_BOLT_THUNDER", 8.0f, 0.55f);
        ExplosionSupport.createExplosion(world, loc, event.definition(), explosion.power(), explosion.fire());

        if (explosion.screenShake()) {
            for (var player : world.getPlayersNear(loc, finalPower * 2)) {
                player.getWorld().playSound(player.getLocation(), "ENTITY_GENERIC_EXPLODE", 2.0f, 0.5f);
            }
        }
    }
}

