package dev.rono.igniscore.block.pizzaoven;

import dev.rono.extensions.shared.strategy.ProcessingGuiSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

final class PizzaOvenSupport {
    private PizzaOvenSupport() {
    }

    static final int BREAD_SLOT = 10;
    static final int TOMATO_SLOT = 12;
    static final int CHEESE_SLOT = 14;
    static final int OUTPUT_SLOT = 16;

    static void tick(PizzaOvenRuntime runtime, BlockDefinition definition, IgnisLocation location) {

        var gui = runtime.registry.blockGui(location);
        if (gui == null) {
            return;
        }
        var inventory = gui.inventory();
        if (!ProcessingGuiSupport.matches(inventory.getItem(BREAD_SLOT), "bread")
                || !ProcessingGuiSupport.matches(inventory.getItem(TOMATO_SLOT), "beetroot")
                || !ProcessingGuiSupport.matches(inventory.getItem(CHEESE_SLOT), "milk_bucket")) {
            return;
        }
        ProcessingGuiSupport.consumeOne(inventory, BREAD_SLOT);
        ProcessingGuiSupport.consumeOne(inventory, TOMATO_SLOT);
        ProcessingGuiSupport.consumeOne(inventory, CHEESE_SLOT);
        ProcessingGuiSupport.setOutput(runtime.context.extensions(), inventory, OUTPUT_SLOT, "cake", 1);
        IgnisWorld world = worldAt(runtime, location);
        IgnisLocation center = Locations.toCenter(location);
        world.spawnParticle(center, "FLAME", 6, 0.3, 0.2, 0.3, 0.02);
        world.playSound(center, "BLOCK_SMOKER_SMOKE", 0.6f, 1.0f);
        TheatricsSupport.sparkle(world, center, "LAVA", 4);
    
    }

    static Component title(PizzaOvenRuntime runtime, BlockDefinition definition) {

        return definition.getTitle() == null ? Component.text("Pizza Oven") : definition.getTitle();
    
    }

    static IgnisWorld worldAt(PizzaOvenRuntime runtime, IgnisLocation location) {

        return runtime.context.extensions().resolveWorld(location);
    
    }
}

