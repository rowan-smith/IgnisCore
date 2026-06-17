package dev.rono.igniscore.region;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.service.IgnisRegionService;

import java.util.List;
import java.util.function.Predicate;

/**
 * Built-in region editing via {@link IgnisWorld}. Always available as a fallback.
 */
public final class IgnisWorldRegionService implements IgnisRegionService {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String providerName() {
        return "ignis-world";
    }

    @Override
    public boolean isWorldEditBacked() {
        return false;
    }

    @Override
    public void breakHollowSphere(IgnisWorld world, IgnisLocation center, int outerRadius, int shellThickness,
                                   boolean staggered, int batchSize, int batchDelayTicks, IgnisScheduler scheduler) {
        List<IgnisLocation> targets = RegionShapeCollector.hollowSphere(world, center, outerRadius, shellThickness);
        RegionBlockBreaker.breakTargets(world, center, targets, staggered, batchSize, batchDelayTicks, scheduler,
                "EXPLOSION", "SMOKE");
    }

    @Override
    public void breakTorus(IgnisWorld world, IgnisLocation center, int majorRadius, int minorRadius,
                            boolean staggered, int batchSize, int batchDelayTicks, IgnisScheduler scheduler) {
        List<IgnisLocation> targets = RegionShapeCollector.torus(world, center, majorRadius, minorRadius);
        RegionBlockBreaker.breakTargets(world, center, targets, staggered, batchSize, batchDelayTicks, scheduler,
                "FLAME", "SMOKE");
    }

    @Override
    public void breakCylinderDown(IgnisWorld world, IgnisLocation center, int radius, int depth,
                                   boolean staggered, int batchSize, int batchDelayTicks, IgnisScheduler scheduler) {
        List<IgnisLocation> targets = RegionShapeCollector.cylinderDown(world, center, radius, depth);
        RegionBlockBreaker.breakTargets(world, center, targets, staggered, batchSize, batchDelayTicks, scheduler,
                "CLOUD", "CRIT");
    }

    @Override
    public void breakUnderwater(IgnisWorld world, IgnisLocation center, int radius,
                                 boolean staggered, int batchSize, int batchDelayTicks, IgnisScheduler scheduler) {
        List<IgnisLocation> targets = RegionShapeCollector.underwaterSphere(world, center, radius);
        RegionBlockBreaker.breakTargets(world, center, targets, staggered, batchSize, batchDelayTicks, scheduler,
                "BUBBLE", "SPLASH");
    }

    @Override
    public void breakWithPredicate(IgnisWorld world, IgnisLocation center, int radius,
                                    Predicate<IgnisLocation> predicate, boolean staggered, int batchSize,
                                    int batchDelayTicks, IgnisScheduler scheduler,
                                    String primaryParticle, String secondaryParticle) {
        List<IgnisLocation> targets = RegionShapeCollector.spherePredicate(world, center, radius, predicate);
        RegionBlockBreaker.breakTargets(world, center, targets, staggered, batchSize, batchDelayTicks, scheduler,
                primaryParticle, secondaryParticle);
    }
}
