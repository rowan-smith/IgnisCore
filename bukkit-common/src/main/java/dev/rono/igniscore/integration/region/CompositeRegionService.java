package dev.rono.igniscore.integration.region;

import com.google.inject.Inject;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.service.IgnisRegionService;
import dev.rono.igniscore.region.IgnisWorldRegionService;

import java.util.function.Predicate;

/**
 * Routes region edits to WorldEdit when available, otherwise the ignis-world fallback.
 */
public final class CompositeRegionService implements IgnisRegionService {
    private final WorldEditRegionService worldEdit;
    private final IgnisWorldRegionService fallback;

    @Inject
    public CompositeRegionService(WorldEditRegionService worldEdit, IgnisWorldRegionService fallback) {
        this.worldEdit = worldEdit;
        this.fallback = fallback;
    }

    @Override
    public boolean isEnabled() {
        return fallback.isEnabled();
    }

    @Override
    public String providerName() {
        return worldEdit.isWorldEditBacked() ? worldEdit.providerName() : fallback.providerName();
    }

    @Override
    public boolean isWorldEditBacked() {
        return worldEdit.isWorldEditBacked();
    }

    @Override
    public void breakHollowSphere(IgnisWorld world, IgnisLocation center, int outerRadius, int shellThickness,
                                   boolean staggered, int batchSize, int batchDelayTicks, IgnisScheduler scheduler) {
        delegate().breakHollowSphere(world, center, outerRadius, shellThickness,
                staggered, batchSize, batchDelayTicks, scheduler);
    }

    @Override
    public void breakTorus(IgnisWorld world, IgnisLocation center, int majorRadius, int minorRadius,
                            boolean staggered, int batchSize, int batchDelayTicks, IgnisScheduler scheduler) {
        delegate().breakTorus(world, center, majorRadius, minorRadius,
                staggered, batchSize, batchDelayTicks, scheduler);
    }

    @Override
    public void breakCylinderDown(IgnisWorld world, IgnisLocation center, int radius, int depth,
                                   boolean staggered, int batchSize, int batchDelayTicks, IgnisScheduler scheduler) {
        delegate().breakCylinderDown(world, center, radius, depth,
                staggered, batchSize, batchDelayTicks, scheduler);
    }

    @Override
    public void breakUnderwater(IgnisWorld world, IgnisLocation center, int radius,
                                 boolean staggered, int batchSize, int batchDelayTicks, IgnisScheduler scheduler) {
        delegate().breakUnderwater(world, center, radius, staggered, batchSize, batchDelayTicks, scheduler);
    }

    @Override
    public void breakWithPredicate(IgnisWorld world, IgnisLocation center, int radius,
                                    Predicate<IgnisLocation> predicate, boolean staggered, int batchSize,
                                    int batchDelayTicks, IgnisScheduler scheduler,
                                    String primaryParticle, String secondaryParticle) {
        delegate().breakWithPredicate(world, center, radius, predicate, staggered,
                batchSize, batchDelayTicks, scheduler, primaryParticle, secondaryParticle);
    }

    private IgnisRegionService delegate() {
        return worldEdit.isWorldEditBacked() ? worldEdit : fallback;
    }
}
