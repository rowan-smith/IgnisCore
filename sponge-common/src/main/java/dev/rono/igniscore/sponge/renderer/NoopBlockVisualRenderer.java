package dev.rono.igniscore.sponge.renderer;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.BlockVisualRenderer;
import dev.rono.igniscore.api.port.IgnisLocation;

public class NoopBlockVisualRenderer implements BlockVisualRenderer {
    @Override
    public void spawnAnimatedDisplay(RuntimeBlockInstance instance) {
    }

    @Override
    public Object spawnStaticDisplay(IgnisLocation location, BlockDefinition definition) {
        return null;
    }

    @Override
    public void updateAnimation(RuntimeBlockInstance instance) {
    }

    @Override
    public void removeDisplay(RuntimeBlockInstance instance) {
    }

    @Override
    public void removeStaticDisplay(Object nativeDisplay) {
    }
}
