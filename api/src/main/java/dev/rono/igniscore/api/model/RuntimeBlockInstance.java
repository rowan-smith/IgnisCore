package dev.rono.igniscore.api.model;

import dev.rono.igniscore.api.port.IgnisDataContainer;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.MapIgnisDataContainer;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Runtime state for a single placed custom block in the world.
 *
 * <p>Each instance ties a {@link BlockDefinition} to a world location, tracks display entities,
 * scheduled tasks, and persistent per-block data. The core runtime creates and owns instances;
 * strategies access them through placed-phase callbacks.</p>
 */
public class RuntimeBlockInstance {
    private final UUID uuid;
    private final String blockDefinitionId;
    private final BlockDefinition definition;
    private final IgnisLocation location;
    private final Set<Integer> displayEntityIds = new HashSet<>();
    private final IgnisDataContainer data;

    private int ticksLeft;
    private Object displayEntity;
    private IgnisTask task;

    /**
     * @param uuid stable instance identifier
     * @param definition block type definition
     * @param location world position of the placed block
     */
    public RuntimeBlockInstance(UUID uuid, BlockDefinition definition, IgnisLocation location) {
        this.uuid = uuid;
        this.definition = definition;
        this.blockDefinitionId = definition.getId();
        this.location = location;
        this.data = new MapIgnisDataContainer();
        this.ticksLeft = 0;
    }

    /**
     * @return stable instance identifier
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * @return in-game type id from the block definition
     */
    public String getBlockDefinitionId() {
        return blockDefinitionId;
    }

    /**
     * @return immutable block type definition for this instance
     */
    public BlockDefinition getDefinition() {
        return definition;
    }

    /**
     * @return world name where this block is placed
     */
    public String getWorldName() {
        return location.worldName();
    }

    /**
     * @return world position of the placed block
     */
    public IgnisLocation getLocation() {
        return location;
    }

    /**
     * @return entity ids registered for block display models (mutable set owned by the instance)
     */
    public Set<Integer> getDisplayEntityIds() {
        return displayEntityIds;
    }

    /**
     * @return persistent per-block data container (NBT-backed on supported platforms)
     */
    public IgnisDataContainer getData() {
        return data;
    }

    /**
     * @return remaining fuse or tick countdown, or zero when inactive
     */
    public int getTicksLeft() {
        return ticksLeft;
    }

    /**
     * @param ticksLeft remaining fuse or tick countdown
     */
    public void setTicksLeft(int ticksLeft) {
        this.ticksLeft = ticksLeft;
    }

    /**
     * Decrements the tick countdown by one.
     */
    public void tick() {
        this.ticksLeft--;
    }

    /**
     * @return platform-specific display entity handle, or {@code null} when not spawned
     */
    public Object getDisplayEntity() {
        return displayEntity;
    }

    /**
     * @param displayEntity platform-specific display entity handle
     */
    public void setDisplayEntity(Object displayEntity) {
        this.displayEntity = displayEntity;
    }

    /**
     * Records an entity id used to render this block's display model.
     *
     * @param entityId platform entity id
     */
    public void registerDisplayEntityId(int entityId) {
        this.displayEntityIds.add(entityId);
    }

    /**
     * @return scheduled repeating or delayed task, or {@code null} when none is active
     */
    public IgnisTask getTask() {
        return task;
    }

    /**
     * @param task scheduled repeating or delayed task
     */
    public void setTask(IgnisTask task) {
        this.task = task;
    }

    /**
     * Stores a boolean flag in the instance data container.
     *
     * @param key flag name
     * @param value flag value
     */
    public void setFlag(String key, boolean value) {
        data.setBoolean(key, value);
    }

    /**
     * Reads a boolean flag from the instance data container.
     *
     * @param key flag name
     * @return stored value, or {@code false} when unset
     */
    public boolean getFlag(String key) {
        return data.getBoolean(key);
    }
}
