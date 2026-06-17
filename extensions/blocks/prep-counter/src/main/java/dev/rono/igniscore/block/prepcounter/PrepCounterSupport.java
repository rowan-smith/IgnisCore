package dev.rono.igniscore.block.prepcounter;

import dev.rono.extensions.shared.strategy.ProcessingGuiSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

final class PrepCounterSupport {
    private PrepCounterSupport() {
    }

    static final int[] INPUT_SLOTS = {10, 11, 12};
    static final int OUTPUT_SLOT = 16;

    static void tick(PrepCounterRuntime runtime, BlockDefinition definition, IgnisLocation location) {

        var gui = runtime.registry.blockGui(location);
        if (gui == null) {
            return;
        }
        var inventory = gui.inventory();
        int foods = 0;
        for (int slot : INPUT_SLOTS) {
            IgnisItem item = inventory.getItem(slot);
            if (ProcessingGuiSupport.matches(item, "bread", "cooked", "apple", "carrot", "potato", "beef", "pork", "chicken", "fish")) {
                foods++;
            }
        }
        if (foods < 3) {
            return;
        }
        for (int slot : INPUT_SLOTS) {
            ProcessingGuiSupport.consumeOne(inventory, slot);
        }
        ProcessingGuiSupport.setOutput(runtime.context.extensions(), inventory, OUTPUT_SLOT, "golden_carrot", 1);
        IgnisWorld world = worldAt(runtime, location);
        IgnisLocation center = Locations.toCenter(location);
        TheatricsSupport.sparkle(world, center, "HAPPY_VILLAGER", 10);
        world.playSound(center, "ENTITY_PLAYER_BURP", 0.5f, 1.2f);
    
    }

    static Component title(PrepCounterRuntime runtime, BlockDefinition definition) {

        return definition.getTitle() == null ? Component.text("Prep Counter") : definition.getTitle();
    
    }

    static IgnisWorld worldAt(PrepCounterRuntime runtime, IgnisLocation location) {

        return runtime.context.extensions().resolveWorld(location);
    
    }
}

