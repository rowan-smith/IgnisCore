package dev.rono.igniscore.block.displaycase;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import net.kyori.adventure.text.Component;

final class DisplayCaseSupport {
    private DisplayCaseSupport() {
    }

    static final int DISPLAY_SLOT = 13;

    static Component title(DisplayCaseRuntime runtime, BlockDefinition definition) {

        return definition.getTitle() == null ? Component.text("Display Case") : definition.getTitle();
    
    }

    static IgnisWorld worldAt(DisplayCaseRuntime runtime, IgnisLocation location) {

        return runtime.context.extensions().resolveWorld(location);
    
    }
}

