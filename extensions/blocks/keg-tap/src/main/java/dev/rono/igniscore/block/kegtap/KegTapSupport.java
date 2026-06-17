package dev.rono.igniscore.block.kegtap;

import dev.rono.extensions.shared.strategy.ProcessingGuiSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

final class KegTapSupport {
    private KegTapSupport() {
    }

    static final int BUCKET_SLOT = 10;
    static final int OUTPUT_SLOT = 16;

    static void tick(KegTapRuntime runtime, BlockDefinition definition, IgnisLocation location) {

        var gui = runtime.registry.blockGui(location);
        if (gui == null) {
            return;
        }
        var inventory = gui.inventory();
        if (!ProcessingGuiSupport.matches(inventory.getItem(BUCKET_SLOT), "water_bucket", "milk_bucket")) {
            return;
        }
        IgnisItem out = inventory.getItem(OUTPUT_SLOT);
        if (out != null && !out.isAir()) {
            return;
        }
        ProcessingGuiSupport.consumeOne(inventory, BUCKET_SLOT);
        ProcessingGuiSupport.setOutput(runtime.context.extensions(), inventory, OUTPUT_SLOT, "potion", 1);
        IgnisWorld world = worldAt(runtime, location);
        world.playSound(Locations.toCenter(location), "ITEM_BOTTLE_FILL", 0.6f, 1.0f);
    
    }

    static Component title(KegTapRuntime runtime, BlockDefinition definition) {

        return definition.getTitle() == null ? Component.text("Keg Tap") : definition.getTitle();
    
    }

    static IgnisWorld worldAt(KegTapRuntime runtime, IgnisLocation location) {

        return runtime.context.extensions().resolveWorld(location);
    
    }
}

