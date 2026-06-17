package dev.rono.igniscore.block.picnicbasket;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.util.Locations;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.kyori.adventure.text.Component;

final class PicnicBasketSupport {
    private PicnicBasketSupport() {
    }

    static final int STORAGE_SLOTS = 6;
    static final Map<String, Long> LAST_OPEN = new ConcurrentHashMap<>();

    static Component title(PicnicBasketRuntime runtime, BlockDefinition definition) {

        return definition.getTitle() == null ? Component.text("Picnic Basket") : definition.getTitle();
    
    }

    static IgnisWorld worldAt(PicnicBasketRuntime runtime, IgnisLocation location) {

        return runtime.context.extensions().resolveWorld(location);
    
    }

    static String blockKey(IgnisLocation location) {
        IgnisLocation block = Locations.toBlock(location);
        return block.worldName() + ":" + (int) block.x() + ":" + (int) block.y() + ":" + (int) block.z();
    }
}

