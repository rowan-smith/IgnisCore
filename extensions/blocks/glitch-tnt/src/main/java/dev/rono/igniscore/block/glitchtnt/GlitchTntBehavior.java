package dev.rono.igniscore.block.glitchtnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.extensions.shared.strategy.PreviewTrickSupport;
import dev.rono.igniscore.api.util.Locations;

final class GlitchTntBehavior {
    private static final String[] MATERIALS = {
            "diamond_block", "gold_block", "emerald_block", "lapis_block",
            "redstone_block", "copper_block", "amethyst_block", "obsidian"
    };
    private final IgnisStrategyContext context;

    GlitchTntBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        int radius = StrategySupport.customInt(def, "glitchRadius", 2);
        int elapsed = ExplosionSupport.elapsedFuseTicks(instance, 80);
        PreviewTrickSupport.cycleBlockPreviews(context.effects(),
                worldAt(center).getPlayersNear(center, 20), center, radius, MATERIALS, elapsed);
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.0f, 1.3f);
        ExplosionSupport.createExplosion(world, loc, instance.getDefinition(), 3.5, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
