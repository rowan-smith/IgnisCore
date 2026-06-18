package dev.rono.igniscore.sponge.listener;

import com.google.inject.Inject;
import dev.rono.igniscore.config.PerformanceSettings;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.service.PlacedBlockPersistenceService;
import dev.rono.igniscore.sponge.adapter.SpongeBridge;
import dev.rono.igniscore.sponge.support.SpongeRuntimeHolder;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.world.chunk.ChunkEvent;
import org.spongepowered.api.world.chunk.WorldChunk;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3i;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpongePlacedBlockRestoreListener {
    private final BlockManager blockManager;
    private final PlacedBlockPersistenceService persistenceService;
    private final dev.rono.igniscore.api.port.PlatformAdapter platformAdapter;
    private final int blocksPerTick;
    private final ArrayDeque<PendingRestore> pendingRestores = new ArrayDeque<>();
    private final AtomicBoolean batchScheduled = new AtomicBoolean();

    @Inject
    public SpongePlacedBlockRestoreListener(BlockManager blockManager,
                                            PlacedBlockPersistenceService persistenceService,
                                            PerformanceSettings performanceSettings,
                                            dev.rono.igniscore.api.port.PlatformAdapter platformAdapter) {
        this.blockManager = blockManager;
        this.persistenceService = persistenceService;
        this.platformAdapter = platformAdapter;
        this.blocksPerTick = performanceSettings.chunkRestoreBlocksPerTick();
    }

    @Listener(order = Order.LATE)
    public void onChunkLoad(ChunkEvent.Load event) {
        enqueueChunk(event.chunk());
    }

    public void restoreLoadedChunks() {
        for (ServerWorld world : SpongeRuntimeHolder.server().worldManager().worlds()) {
            for (String chunkKey : persistenceService.chunkKeysForWorld(world.key().asString())) {
                String[] parts = chunkKey.split(",");
                if (parts.length != 2) {
                    continue;
                }
                try {
                    int chunkX = Integer.parseInt(parts[0]);
                    int chunkZ = Integer.parseInt(parts[1]);
                    WorldChunk chunk = world.chunk(Vector3i.from(chunkX, 0, chunkZ));
                    enqueueChunk(chunk);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        ensureBatchTaskRunning();
    }

    private void enqueueChunk(WorldChunk chunk) {
        ServerWorld world = (ServerWorld) chunk.world();
        Map<String, String> entries = persistenceService.entriesInChunk(
                world.key().asString(),
                chunk.chunkPosition().x(),
                chunk.chunkPosition().z());
        if (entries.isEmpty()) {
            return;
        }

        for (Map.Entry<String, String> entry : entries.entrySet()) {
            pendingRestores.add(new PendingRestore(chunk, entry.getKey(), entry.getValue()));
        }
        ensureBatchTaskRunning();
    }

    private void ensureBatchTaskRunning() {
        if (pendingRestores.isEmpty() || !batchScheduled.compareAndSet(false, true)) {
            return;
        }
        platformAdapter.getScheduler().runGlobal(this::processBatchAndContinue);
    }

    private void processBatchAndContinue() {
        processBatch();
        if (pendingRestores.isEmpty()) {
            batchScheduled.set(false);
            return;
        }
        platformAdapter.getScheduler().runGlobalLater(this::processBatchAndContinue, 1L);
    }

    private void processBatch() {
        int processed = 0;
        while (processed < blocksPerTick && !pendingRestores.isEmpty()) {
            PendingRestore restore = pendingRestores.poll();
            if (restore != null && tryRestore(restore)) {
                processed++;
            }
        }
    }

    private boolean tryRestore(PendingRestore restore) {
        ServerLocation location = parseLocation(restore.chunk(), restore.blockKey());
        if (location == null || blockManager.getPlacedBlockType(SpongeBridge.toIgnis(location)) != null) {
            return false;
        }

        if (!location.createSnapshot().state().type().equals(BlockTypes.BARRIER.get())) {
            persistenceService.removePlacement(SpongeBridge.toIgnis(location));
            return false;
        }

        blockManager.restorePlacedBlock(SpongeBridge.toIgnis(location), restore.typeId());
        return true;
    }

    private ServerLocation parseLocation(WorldChunk chunk, String key) {
        String[] parts = key.split(",");
        if (parts.length != 3) {
            return null;
        }
        try {
            ServerWorld world = (ServerWorld) chunk.world();
            return world.location(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private record PendingRestore(WorldChunk chunk, String blockKey, String typeId) {
    }
}
