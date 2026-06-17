package dev.rono.extensions.shared.strategy;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.service.IgnisRegionService;

import java.util.function.Predicate;

/**
 * Delegates shaped region edits to {@link IgnisRegionService} (WorldEdit when available,
 * otherwise ignis-world fallback).
 */
public final class BlockBlastSupport {
    private BlockBlastSupport() {
    }

    public static void breakHollowSphere(IgnisRegionService region,
                                         IgnisWorld world,
                                         IgnisLocation center,
                                         int outerRadius,
                                         int shellThickness,
                                         boolean staggered,
                                         int batchSize,
                                         int batchDelayTicks,
                                         IgnisScheduler scheduler) {
        region.breakHollowSphere(world, center, outerRadius, shellThickness,
                staggered, batchSize, batchDelayTicks, scheduler);
    }

    public static void breakTorus(IgnisRegionService region,
                                   IgnisWorld world,
                                   IgnisLocation center,
                                   int majorRadius,
                                   int minorRadius,
                                   boolean staggered,
                                   int batchSize,
                                   int batchDelayTicks,
                                   IgnisScheduler scheduler) {
        region.breakTorus(world, center, majorRadius, minorRadius,
                staggered, batchSize, batchDelayTicks, scheduler);
    }

    public static void breakCylinderDown(IgnisRegionService region,
                                          IgnisWorld world,
                                          IgnisLocation center,
                                          int radius,
                                          int depth,
                                          boolean staggered,
                                          int batchSize,
                                          int batchDelayTicks,
                                          IgnisScheduler scheduler) {
        region.breakCylinderDown(world, center, radius, depth,
                staggered, batchSize, batchDelayTicks, scheduler);
    }

    public static void breakUnderwater(IgnisRegionService region,
                                        IgnisWorld world,
                                        IgnisLocation center,
                                        int radius,
                                        boolean staggered,
                                        int batchSize,
                                        int batchDelayTicks,
                                        IgnisScheduler scheduler) {
        region.breakUnderwater(world, center, radius, staggered, batchSize, batchDelayTicks, scheduler);
    }

    public static void breakWithPredicate(IgnisRegionService region,
                                           IgnisWorld world,
                                           IgnisLocation center,
                                           int radius,
                                           Predicate<IgnisLocation> predicate,
                                           boolean staggered,
                                           int batchSize,
                                           int batchDelayTicks,
                                           IgnisScheduler scheduler,
                                           String primaryParticle,
                                           String secondaryParticle) {
        region.breakWithPredicate(world, center, radius, predicate, staggered,
                batchSize, batchDelayTicks, scheduler, primaryParticle, secondaryParticle);
    }
}
