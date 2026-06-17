package dev.rono.igniscore.sponge.integration.region;

import com.google.inject.Inject;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockTypes;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.service.IgnisRegionService;
import dev.rono.igniscore.region.IgnisWorldRegionService;
import dev.rono.igniscore.region.RegionBlockBreaker;
import dev.rono.igniscore.region.RegionShapeCollector;
import dev.rono.igniscore.sponge.SpongePluginHost;
import dev.rono.igniscore.sponge.adapter.SpongeIgnisWorld;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.world.server.ServerWorld;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Predicate;

/**
 * WorldEdit-backed region editing when the WorldEdit plugin is present on Sponge.
 */
public final class SpongeWorldEditRegionService implements IgnisRegionService {
    private final boolean enabled;
    private final IgnisWorldRegionService fallback;
    private final Logger logger;
    private final Method adaptWorld;

    @Inject
    public SpongeWorldEditRegionService(SpongePluginHost plugin, IgnisWorldRegionService fallback) {
        this.fallback = fallback;
        this.logger = plugin.getLogger();
        Game game = plugin.game();
        boolean active = false;
        Method adapt = null;

        if (game.pluginManager().plugin("worldedit").isPresent()) {
            try {
                Class.forName("com.sk89q.worldedit.WorldEdit");
                Class<?> adapter = Class.forName("com.sk89q.worldedit.sponge.SpongeAdapter");
                adapt = adapter.getMethod("adapt", ServerWorld.class);
                active = true;
                logger.info("WorldEdit region integration enabled on Sponge.");
            } catch (Throwable error) {
                logger.warn("WorldEdit present but Sponge API unavailable: {}", error.getMessage());
            }
        }

        this.enabled = active;
        this.adaptWorld = adapt;
        if (!enabled) {
            logger.info("WorldEdit not found. Region edits use ignis-world fallback.");
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String providerName() {
        return enabled ? "WorldEdit" : fallback.providerName();
    }

    @Override
    public boolean isWorldEditBacked() {
        return enabled;
    }

    @Override
    public void breakHollowSphere(IgnisWorld world, IgnisLocation center, int outerRadius, int shellThickness,
                                   boolean staggered, int batchSize, int batchDelayTicks, IgnisScheduler scheduler) {
        if (!enabled) {
            fallback.breakHollowSphere(world, center, outerRadius, shellThickness, staggered,
                    batchSize, batchDelayTicks, scheduler);
            return;
        }
        applyBreak(world, center, RegionShapeCollector.hollowSphere(world, center, outerRadius, shellThickness),
                staggered, batchSize, batchDelayTicks, scheduler, "EXPLOSION", "SMOKE");
    }

    @Override
    public void breakTorus(IgnisWorld world, IgnisLocation center, int majorRadius, int minorRadius,
                            boolean staggered, int batchSize, int batchDelayTicks, IgnisScheduler scheduler) {
        if (!enabled) {
            fallback.breakTorus(world, center, majorRadius, minorRadius, staggered,
                    batchSize, batchDelayTicks, scheduler);
            return;
        }
        applyBreak(world, center, RegionShapeCollector.torus(world, center, majorRadius, minorRadius),
                staggered, batchSize, batchDelayTicks, scheduler, "FLAME", "SMOKE");
    }

    @Override
    public void breakCylinderDown(IgnisWorld world, IgnisLocation center, int radius, int depth,
                                   boolean staggered, int batchSize, int batchDelayTicks, IgnisScheduler scheduler) {
        if (!enabled) {
            fallback.breakCylinderDown(world, center, radius, depth, staggered,
                    batchSize, batchDelayTicks, scheduler);
            return;
        }
        applyBreak(world, center, RegionShapeCollector.cylinderDown(world, center, radius, depth),
                staggered, batchSize, batchDelayTicks, scheduler, "CLOUD", "CRIT");
    }

    @Override
    public void breakUnderwater(IgnisWorld world, IgnisLocation center, int radius,
                                 boolean staggered, int batchSize, int batchDelayTicks, IgnisScheduler scheduler) {
        if (!enabled) {
            fallback.breakUnderwater(world, center, radius, staggered, batchSize, batchDelayTicks, scheduler);
            return;
        }
        applyBreak(world, center, RegionShapeCollector.underwaterSphere(world, center, radius),
                staggered, batchSize, batchDelayTicks, scheduler, "BUBBLE", "SPLASH");
    }

    @Override
    public void breakWithPredicate(IgnisWorld world, IgnisLocation center, int radius,
                                    Predicate<IgnisLocation> predicate, boolean staggered, int batchSize,
                                    int batchDelayTicks, IgnisScheduler scheduler,
                                    String primaryParticle, String secondaryParticle) {
        if (!enabled) {
            fallback.breakWithPredicate(world, center, radius, predicate, staggered,
                    batchSize, batchDelayTicks, scheduler, primaryParticle, secondaryParticle);
            return;
        }
        applyBreak(world, center, RegionShapeCollector.spherePredicate(world, center, radius, predicate),
                staggered, batchSize, batchDelayTicks, scheduler, primaryParticle, secondaryParticle);
    }

    private void applyBreak(IgnisWorld world,
                             IgnisLocation center,
                             List<IgnisLocation> targets,
                             boolean staggered,
                             int batchSize,
                             int batchDelayTicks,
                             IgnisScheduler scheduler,
                             String primaryParticle,
                             String secondaryParticle) {
        if (targets.isEmpty()) {
            return;
        }
        if (staggered && scheduler != null) {
            RegionBlockBreaker.breakTargets(world, center, targets, true, batchSize, batchDelayTicks, scheduler,
                    primaryParticle, secondaryParticle);
            return;
        }
        writeBlocks(world, targets);
        int step = Math.max(1, targets.size() / 40);
        for (int i = 0; i < targets.size(); i += step) {
            IgnisLocation particleLoc = targets.get(i).add(0.5, 0.5, 0.5);
            world.spawnParticle(particleLoc, primaryParticle, 2, 0.1, 0.1, 0.1, 0.01);
            world.spawnParticle(particleLoc, secondaryParticle, 1, 0.05, 0.05, 0.05, 0.02);
        }
    }

    private void writeBlocks(IgnisWorld world, List<IgnisLocation> targets) {
        ServerWorld spongeWorld = resolveServerWorld(world);
        if (spongeWorld == null || adaptWorld == null) {
            for (IgnisLocation target : targets) {
                world.setBlockMaterialKey(target, "air");
            }
            return;
        }
        try {
            Object weWorld = adaptWorld.invoke(null, spongeWorld);
            try (EditSession session = WorldEdit.getInstance().newEditSession(
                    (com.sk89q.worldedit.world.World) weWorld)) {
                for (IgnisLocation target : targets) {
                    session.setBlock(
                            BlockVector3.at((int) Math.floor(target.x()),
                                    (int) Math.floor(target.y()),
                                    (int) Math.floor(target.z())),
                            BlockTypes.AIR.getDefaultState());
                }
            }
        } catch (Exception error) {
            logger.warn("WorldEdit region edit failed, falling back to ignis-world: {}", error.getMessage());
            for (IgnisLocation target : targets) {
                world.setBlockMaterialKey(target, "air");
            }
        }
    }

    private ServerWorld resolveServerWorld(IgnisWorld world) {
        if (world instanceof SpongeIgnisWorld spongeWorld) {
            return spongeWorld.getHandle();
        }
        return null;
    }
}
