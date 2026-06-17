package dev.rono.igniscore.block.fortunecookiemaker;

import dev.rono.extensions.shared.strategy.ProcessingGuiSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

final class FortuneCookieMakerSupport {
    private FortuneCookieMakerSupport() {
    }

    static final int WHEAT_SLOT = 11;
    static final int PAPER_SLOT = 15;
    static final int OUTPUT_SLOT = 17;
    static final String[] FORTUNES = {
            "A surprise awaits behind the next door.",
            "Beware the creeper in plain sight.",
            "Your build will inspire the server.",
            "Share loot and luck will follow.",
            "Dig down — but not too far."
    };

    static void tick(FortuneCookieMakerRuntime runtime, BlockDefinition definition, IgnisLocation location) {

        var gui = runtime.registry.blockGui(location);
        if (gui == null) {
            return;
        }
        var inventory = gui.inventory();
        if (!ProcessingGuiSupport.matches(inventory.getItem(WHEAT_SLOT), "wheat")
                || !ProcessingGuiSupport.matches(inventory.getItem(PAPER_SLOT), "paper")) {
            return;
        }
        ProcessingGuiSupport.consumeOne(inventory, WHEAT_SLOT);
        ProcessingGuiSupport.consumeOne(inventory, PAPER_SLOT);
        ProcessingGuiSupport.setOutput(runtime.context.extensions(), inventory, OUTPUT_SLOT, "cookie", 1);
        IgnisWorld world = worldAt(runtime, location);
        world.playSound(Locations.toCenter(location), "ENTITY_GENERIC_EAT", 0.5f, 1.3f);
    
    }

    static Component title(FortuneCookieMakerRuntime runtime, BlockDefinition definition) {

        return definition.getTitle() == null ? Component.text("Fortune Cookie Maker") : definition.getTitle();
    
    }

    static IgnisWorld worldAt(FortuneCookieMakerRuntime runtime, IgnisLocation location) {

        return runtime.context.extensions().resolveWorld(location);
    
    }
}

