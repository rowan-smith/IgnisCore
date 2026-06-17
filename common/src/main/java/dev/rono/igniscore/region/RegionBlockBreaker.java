package dev.rono.igniscore.region;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisScheduler;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.IgnisWorld;

import java.util.Comparator;
import java.util.List;

/**
 * Applies block breaks via {@link IgnisWorld} with optional staggered batching.
 */
public final class RegionBlockBreaker {
    private RegionBlockBreaker() {
    }

    public static void breakTargets(IgnisWorld world,
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

        targets.sort(Comparator.comparingDouble(loc -> distanceSquared(loc, center)));

        if (!staggered || scheduler == null) {
            for (IgnisLocation target : targets) {
                breakBlock(world, target, primaryParticle, secondaryParticle);
            }
            return;
        }

        int safeBatch = Math.max(1, batchSize);
        int delay = Math.max(1, batchDelayTicks);
        int[] index = {0};
        IgnisTask[] taskRef = {null};
        taskRef[0] = scheduler.runRepeating(center, () -> {
            int end = Math.min(index[0] + safeBatch, targets.size());
            for (int i = index[0]; i < end; i++) {
                breakBlock(world, targets.get(i), primaryParticle, secondaryParticle);
            }
            index[0] = end;
            if (index[0] >= targets.size() && taskRef[0] != null) {
                taskRef[0].cancel();
            }
        }, 0L, delay);
    }

    public static void breakBlock(IgnisWorld world, IgnisLocation block,
                                   String primaryParticle, String secondaryParticle) {
        IgnisLocation particleLoc = block.add(0.5, 0.5, 0.5);
        world.spawnParticle(particleLoc, primaryParticle, 3, 0.15, 0.15, 0.15, 0.01);
        world.spawnParticle(particleLoc, secondaryParticle, 2, 0.1, 0.1, 0.1, 0.02);
        world.setBlockMaterialKey(block, "air");
    }

    private static double distanceSquared(IgnisLocation a, IgnisLocation b) {
        double dx = a.x() - b.x();
        double dy = a.y() - b.y();
        double dz = a.z() - b.z();
        return dx * dx + dy * dy + dz * dz;
    }
}
