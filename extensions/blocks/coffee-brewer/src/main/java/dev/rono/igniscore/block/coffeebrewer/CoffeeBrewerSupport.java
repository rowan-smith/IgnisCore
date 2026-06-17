package dev.rono.igniscore.block.coffeebrewer;

import dev.rono.extensions.shared.strategy.ProcessingGuiSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

final class CoffeeBrewerSupport {
    private CoffeeBrewerSupport() {
    }

    static final int COCOA_SLOT = 11;
    static final int BOTTLE_SLOT = 15;
    static final int OUTPUT_SLOT = 17;

    static void tick(CoffeeBrewerRuntime runtime, BlockDefinition definition, IgnisLocation location) {

        var gui = runtime.registry.blockGui(location);
        if (gui == null) {
            return;
        }
        var inventory = gui.inventory();
        if (!ProcessingGuiSupport.matches(inventory.getItem(COCOA_SLOT), "cocoa")
                || !ProcessingGuiSupport.matches(inventory.getItem(BOTTLE_SLOT), "glass_bottle", "potion")) {
            return;
        }
        ProcessingGuiSupport.consumeOne(inventory, COCOA_SLOT);
        ProcessingGuiSupport.consumeOne(inventory, BOTTLE_SLOT);
        ProcessingGuiSupport.setOutput(runtime.context.extensions(), inventory, OUTPUT_SLOT, "potion", 1);
        IgnisWorld world = worldAt(runtime, location);
        IgnisLocation center = Locations.toCenter(location);
        world.spawnParticle(center, "DRIPPING_HONEY", 5, 0.2, 0.3, 0.2, 0.01);
        world.playSound(center, "BLOCK_BREWING_STAND_BREW", 0.6f, 1.2f);
    
    }

    static Component title(CoffeeBrewerRuntime runtime, BlockDefinition definition) {

        return definition.getTitle() == null ? Component.text("Coffee Brewer") : definition.getTitle();
    
    }

    static IgnisWorld worldAt(CoffeeBrewerRuntime runtime, IgnisLocation location) {

        return runtime.context.extensions().resolveWorld(location);
    
    }
}

