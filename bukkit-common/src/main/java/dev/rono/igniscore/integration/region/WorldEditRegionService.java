package dev.rono.igniscore.integration.region;

import com.google.inject.Inject;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.service.IgnisRegionService;
import dev.rono.igniscore.region.IgnisWorldRegionService;
import dev.rono.igniscore.region.RegionBlockBreaker;
import dev.rono.igniscore.region.RegionShapeCollector;
import dev.rono.igniscore.spigot.adapter.BukkitIgnisWorld;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * WorldEdit-backed region editing when the WorldEdit plugin is present.
 */
public final class WorldEditRegionService implements IgnisRegionService {
    private final boolean enabled;
    private final IgnisWorldRegionService fallback;
    private final Logger logger;
    private final Method adaptBukkitWorld;
    private final Method worldEditGetInstance;
    private final Method newEditSession;
    private final Method blockVector3At;
    private final Object airBlockState;
    private final Method sessionSetBlock;
    private final Method sessionClose;

    @Inject
    public WorldEditRegionService(Plugin plugin, IgnisWorldRegionService fallback) {
        this.fallback = fallback;
        this.logger = plugin.getLogger();
        Method adapt = null;
        Method getInstance = null;
        Method createSession = null;
        Method vectorAt = null;
        Object airState = null;
        Method setBlock = null;
        Method close = null;
        boolean active = false;

        if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit")
                || Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {
            try {
                Class<?> worldEditClass = Class.forName("com.sk89q.worldedit.WorldEdit");
                Class<?> bukkitAdapterClass = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
                Class<?> blockVector3Class = Class.forName("com.sk89q.worldedit.math.BlockVector3");
                Class<?> blockTypesClass = Class.forName("com.sk89q.worldedit.world.block.BlockTypes");
                Class<?> blockStateHolderClass = Class.forName("com.sk89q.worldedit.world.block.BlockStateHolder");
                Class<?> weWorldClass = Class.forName("com.sk89q.worldedit.world.World");
                Class<?> editSessionClass = Class.forName("com.sk89q.worldedit.EditSession");

                adapt = bukkitAdapterClass.getMethod("adapt", World.class);
                getInstance = worldEditClass.getMethod("getInstance");
                createSession = worldEditClass.getMethod("newEditSession", weWorldClass);
                vectorAt = blockVector3Class.getMethod("at", int.class, int.class, int.class);
                Object airBlockType = blockTypesClass.getField("AIR").get(null);
                airState = airBlockType.getClass().getMethod("getDefaultState").invoke(airBlockType);
                setBlock = editSessionClass.getMethod("setBlock", blockVector3Class, blockStateHolderClass);
                close = editSessionClass.getMethod("close");
                active = true;
                logger.info("WorldEdit region integration enabled.");
            } catch (Throwable error) {
                logger.warning("WorldEdit present but API unavailable: " + error.getMessage());
            }
        } else {
            logger.info("WorldEdit not found. Region edits use ignis-world fallback.");
        }

        this.enabled = active;
        this.adaptBukkitWorld = adapt;
        this.worldEditGetInstance = getInstance;
        this.newEditSession = createSession;
        this.blockVector3At = vectorAt;
        this.airBlockState = airState;
        this.sessionSetBlock = setBlock;
        this.sessionClose = close;
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
        World bukkitWorld = resolveBukkitWorld(world);
        if (bukkitWorld == null) {
            for (IgnisLocation target : targets) {
                world.setBlockMaterialKey(target, "air");
            }
            return;
        }
        try {
            Object weWorld = adaptBukkitWorld.invoke(null, bukkitWorld);
            Object worldEdit = worldEditGetInstance.invoke(null);
            Object session = newEditSession.invoke(worldEdit, weWorld);
            try {
                for (IgnisLocation target : targets) {
                    Object vector = blockVector3At.invoke(null,
                            (int) Math.floor(target.x()),
                            (int) Math.floor(target.y()),
                            (int) Math.floor(target.z()));
                    sessionSetBlock.invoke(session, vector, airBlockState);
                }
            } finally {
                sessionClose.invoke(session);
            }
        } catch (ReflectiveOperationException error) {
            logger.warning("WorldEdit region edit failed, falling back to ignis-world: " + error.getMessage());
            for (IgnisLocation target : targets) {
                world.setBlockMaterialKey(target, "air");
            }
        }
    }

    private World resolveBukkitWorld(IgnisWorld world) {
        if (world instanceof BukkitIgnisWorld bukkitWorld) {
            return bukkitWorld.getHandle();
        }
        return Bukkit.getWorld(world.getName());
    }
}
