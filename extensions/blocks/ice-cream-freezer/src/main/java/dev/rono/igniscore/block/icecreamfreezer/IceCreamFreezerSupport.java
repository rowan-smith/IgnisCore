package dev.rono.igniscore.block.icecreamfreezer;

import dev.rono.extensions.shared.strategy.ProcessingGuiSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

final class IceCreamFreezerSupport {
    private IceCreamFreezerSupport() {
    }

    static final int SNOW_SLOT = 10;
    static final int BERRY_SLOT = 12;
    static final int BUCKET_SLOT = 14;
    static final int OUTPUT_SLOT = 16;

    static void tick(IceCreamFreezerRuntime runtime, BlockDefinition definition, IgnisLocation location) {

        var gui = runtime.registry.blockGui(location);
        if (gui == null) {
            return;
        }
        var inventory = gui.inventory();
        if (!ProcessingGuiSupport.matches(inventory.getItem(SNOW_SLOT), "snow_block", "snow")
                || !ProcessingGuiSupport.matches(inventory.getItem(BERRY_SLOT), "sweet_berries")
                || !ProcessingGuiSupport.matches(inventory.getItem(BUCKET_SLOT), "bucket", "milk_bucket")) {
            return;
        }
        ProcessingGuiSupport.consumeOne(inventory, SNOW_SLOT);
        ProcessingGuiSupport.consumeOne(inventory, BERRY_SLOT);
        ProcessingGuiSupport.consumeOne(inventory, BUCKET_SLOT);
        ProcessingGuiSupport.setOutput(runtime.context.extensions(), inventory, OUTPUT_SLOT, "bowl", 1);
        IgnisWorld world = worldAt(runtime, location);
        IgnisLocation center = Locations.toCenter(location);
        TheatricsSupport.sparkle(world, center, "CLOUD", 8);
        world.playSound(center, "BLOCK_POWDER_SNOW_STEP", 0.7f, 1.3f);
    
    }

    static Component title(IceCreamFreezerRuntime runtime, BlockDefinition definition) {

        return definition.getTitle() == null ? Component.text("Ice Cream Freezer") : definition.getTitle();
    
    }

    static IgnisWorld worldAt(IceCreamFreezerRuntime runtime, IgnisLocation location) {

        return runtime.context.extensions().resolveWorld(location);
    
    }
}

