package dev.rono.igniscore.block.spicerack;

import dev.rono.extensions.shared.strategy.ProcessingGuiSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.util.Locations;

final class SpiceRackOnBlockInteractListener implements OnBlockInteractListener {
    private final SpiceRackRuntime runtime;

    SpiceRackOnBlockInteractListener(SpiceRackRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.action() != CustomBlockAction.OPEN) {
            return;
        }
        runtime.registry.openBlock(event.player(), event.block().location());
        var gui = runtime.registry.blockGui(event.block().location());
        if (gui == null) {
            return;
        }
        var inventory = gui.inventory();
        if (ProcessingGuiSupport.matches(inventory.getItem(SpiceRackSupport.FOOD_SLOT), "bread", "cooked", "apple", "carrot", "beef")
                && ProcessingGuiSupport.matches(inventory.getItem(SpiceRackSupport.SPICE_SLOT), "spider_eye", "glow_berries", "sugar", "cocoa")) {
            event.player().applyPotionEffect("HASTE", 200, 0);
            event.player().applyPotionEffect("SATURATION", 100, 0);
            ProcessingGuiSupport.consumeOne(inventory, SpiceRackSupport.SPICE_SLOT);
            IgnisWorld world = SpiceRackSupport.worldAt(runtime, event.block().location());
            IgnisLocation center = Locations.toCenter(event.block().location());
            TheatricsSupport.sparkle(world, center, "FIREWORK", 6);
            world.playSound(center, "ENTITY_GENERIC_EAT", 0.8f, 1.1f);
            event.player().sendMessage("<gold>Spiced plate grants haste and saturation.</gold>");
        }
    }
}

