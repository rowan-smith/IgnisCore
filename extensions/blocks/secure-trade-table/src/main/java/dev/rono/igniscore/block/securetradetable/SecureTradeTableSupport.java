package dev.rono.igniscore.block.securetradetable;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import net.kyori.adventure.text.Component;

final class SecureTradeTableSupport {
    private SecureTradeTableSupport() {
    }

    static Component title(SecureTradeTableRuntime runtime, BlockDefinition definition) {

        return definition.getTitle() == null ? Component.text("Secure Trade") : definition.getTitle();
    
    }

    static IgnisWorld worldAt(SecureTradeTableRuntime runtime, IgnisLocation location) {

        return runtime.context.extensions().resolveWorld(location);
    
    }
}

