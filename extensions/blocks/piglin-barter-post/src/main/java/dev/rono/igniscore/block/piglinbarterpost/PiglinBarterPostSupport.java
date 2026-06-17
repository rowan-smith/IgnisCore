package dev.rono.igniscore.block.piglinbarterpost;

import dev.rono.extensions.shared.strategy.ProcessingGuiSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

final class PiglinBarterPostSupport {
    private PiglinBarterPostSupport() {
    }

    static final int INPUT_SLOT = 10;
    static final int OUTPUT_START = 14;

    static void tick(PiglinBarterPostRuntime runtime, BlockDefinition definition, IgnisLocation location) {

        var gui = runtime.registry.blockGui(location);
        if (gui == null) {
            return;
        }
        var inventory = gui.inventory();
        IgnisItem input = inventory.getItem(INPUT_SLOT);
        if (!ProcessingGuiSupport.matches(input, "gold_ingot")) {
            return;
        }
        ProcessingGuiSupport.consumeOne(inventory, INPUT_SLOT);
        String[] loot = {"ender_pearl", "crying_obsidian", "spectral_arrow", "gilded_blackstone", "iron_nugget"};
        String reward = loot[(int) (Math.random() * loot.length)];
        ProcessingGuiSupport.setOutput(runtime.context.extensions(), inventory, OUTPUT_START, reward, 1);
        IgnisWorld world = worldAt(runtime, location);
        IgnisLocation center = Locations.toCenter(location);
        TheatricsSupport.sparkle(world, center, "CRIMSON_SPORE", 8);
        world.playSound(center, "ENTITY_PIGLIN_ADMIRING_ITEM", 0.8f, 1.0f);
    
    }

    static Component title(PiglinBarterPostRuntime runtime, BlockDefinition definition) {

        return definition.getTitle() == null ? Component.text("Piglin Barter Post") : definition.getTitle();
    
    }

    static IgnisWorld worldAt(PiglinBarterPostRuntime runtime, IgnisLocation location) {

        return runtime.context.extensions().resolveWorld(location);
    
    }
}

