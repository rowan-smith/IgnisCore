package dev.rono.igniscore.block.picnicbasket;

import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.util.Locations;

final class PicnicBasketOnBlockInteractListener implements OnBlockInteractListener {
    private final PicnicBasketRuntime runtime;

    PicnicBasketOnBlockInteractListener(PicnicBasketRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.action() != CustomBlockAction.OPEN) {
            return;
        }
        String key = PicnicBasketSupport.blockKey(event.block().location());
        long now = System.currentTimeMillis();
        Long previous = PicnicBasketSupport.LAST_OPEN.put(key, now);
        runtime.registry.openBlock(event.player(), event.block().location());
        if (previous != null && now - previous < 5000L) {
            IgnisWorld world = PicnicBasketSupport.worldAt(runtime, event.block().location());
            IgnisLocation center = Locations.toCenter(event.block().location());
            for (IgnisPlayer nearby : world.getPlayersNear(center, 6.0)) {
                nearby.applyPotionEffect("SATURATION", 100, 0);
                nearby.sendMessage("<gold>Shared picnic — saturation boost!</gold>");
            }
            TheatricsSupport.sparkle(world, center, "HEART", 10);
        }
    }
}

