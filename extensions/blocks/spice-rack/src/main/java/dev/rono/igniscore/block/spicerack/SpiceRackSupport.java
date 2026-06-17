package dev.rono.igniscore.block.spicerack;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import net.kyori.adventure.text.Component;

final class SpiceRackSupport {
    private SpiceRackSupport() {
    }

    static final int FOOD_SLOT = 11;
    static final int SPICE_SLOT = 15;

    static Component title(SpiceRackRuntime runtime, BlockDefinition definition) {

        return definition.getTitle() == null ? Component.text("Spice Rack") : definition.getTitle();
    
    }

    static IgnisWorld worldAt(SpiceRackRuntime runtime, IgnisLocation location) {

        return runtime.context.extensions().resolveWorld(location);
    
    }
}

