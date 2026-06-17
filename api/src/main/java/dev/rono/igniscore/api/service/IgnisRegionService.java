package dev.rono.igniscore.api.service;

import dev.rono.igniscore.api.integration.IgnisIntegration;
import dev.rono.igniscore.api.integration.IgnisIntegrations;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.port.IgnisWorld;

import java.util.function.Predicate;

/**
 * WorldEdit-style region block editing. Implementations may delegate to WorldEdit when
 * present, otherwise fall back to direct {@link IgnisWorld} block mutation.
 */
public interface IgnisRegionService extends IgnisIntegration {

    @Override
    default String integrationId() {
        return IgnisIntegrations.REGION;
    }

    void breakHollowSphere(IgnisWorld world,
                           IgnisLocation center,
                           int outerRadius,
                           int shellThickness,
                           boolean staggered,
                           int batchSize,
                           int batchDelayTicks,
                           IgnisScheduler scheduler);

    void breakTorus(IgnisWorld world,
                    IgnisLocation center,
                    int majorRadius,
                    int minorRadius,
                    boolean staggered,
                    int batchSize,
                    int batchDelayTicks,
                    IgnisScheduler scheduler);

    void breakCylinderDown(IgnisWorld world,
                           IgnisLocation center,
                           int radius,
                           int depth,
                           boolean staggered,
                           int batchSize,
                           int batchDelayTicks,
                           IgnisScheduler scheduler);

    void breakUnderwater(IgnisWorld world,
                         IgnisLocation center,
                         int radius,
                         boolean staggered,
                         int batchSize,
                         int batchDelayTicks,
                         IgnisScheduler scheduler);

    void breakWithPredicate(IgnisWorld world,
                            IgnisLocation center,
                            int radius,
                            Predicate<IgnisLocation> predicate,
                            boolean staggered,
                            int batchSize,
                            int batchDelayTicks,
                            IgnisScheduler scheduler,
                            String primaryParticle,
                            String secondaryParticle);

    /**
     * Whether the active provider is WorldEdit (or FAWE) rather than the built-in fallback.
     */
    boolean isWorldEditBacked();
}
