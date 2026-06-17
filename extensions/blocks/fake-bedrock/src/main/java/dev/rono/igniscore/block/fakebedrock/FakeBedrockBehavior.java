package dev.rono.igniscore.block.fakebedrock;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;

final class FakeBedrockBehavior {
    private final IgnisStrategyContext context;

    FakeBedrockBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        IgnisWorld world = worldAt(location);
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }
                    IgnisLocation shell = location.add(x, y, z);
                    for (IgnisPlayer player : world.getPlayersNear(Locations.toCenter(location), 20)) {
                        context.effects().showBlockPreview(player, shell, "bedrock");
                    }
                }
            }
        }
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.5f, 0.8f);
        world.createExplosion(loc, 5.0f, false, true);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
