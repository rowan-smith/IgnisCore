package dev.rono.igniscore.config;

public record PerformanceSettings(
        int chunkRestoreBlocksPerTick,
        int visualRefreshBlocksPerTick,
        int resourcePackRetainCount) {

    public static final int DEFAULT_CHUNK_RESTORE_BLOCKS_PER_TICK = 16;
    public static final int DEFAULT_VISUAL_REFRESH_BLOCKS_PER_TICK = 32;
    public static final int DEFAULT_RESOURCE_PACK_RETAIN_COUNT = 3;

    public PerformanceSettings {
        chunkRestoreBlocksPerTick = Math.max(1, chunkRestoreBlocksPerTick);
        visualRefreshBlocksPerTick = Math.max(1, visualRefreshBlocksPerTick);
        resourcePackRetainCount = Math.max(1, resourcePackRetainCount);
    }

    public static PerformanceSettings defaults() {
        return new PerformanceSettings(
                DEFAULT_CHUNK_RESTORE_BLOCKS_PER_TICK,
                DEFAULT_VISUAL_REFRESH_BLOCKS_PER_TICK,
                DEFAULT_RESOURCE_PACK_RETAIN_COUNT);
    }

    public static PerformanceSettings fromValues(int chunkRestoreBlocksPerTick,
                                                 int visualRefreshBlocksPerTick,
                                                 int resourcePackRetainCount) {
        return new PerformanceSettings(
                chunkRestoreBlocksPerTick,
                visualRefreshBlocksPerTick,
                resourcePackRetainCount);
    }
}
