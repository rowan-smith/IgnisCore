package dev.rono.igniscore.api.model;

import dev.rono.igniscore.api.port.IgnisDataContainer;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.MapIgnisDataContainer;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents an active custom block in the world with persistent metadata and runtime state.
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

    public RuntimeBlockInstance(UUID uuid, BlockDefinition definition, IgnisLocation location) {
        this.uuid = uuid;
        this.definition = definition;
        this.blockDefinitionId = definition.getId();
        this.location = location;
        this.data = new MapIgnisDataContainer();
        this.ticksLeft = 0;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getBlockDefinitionId() {
        return blockDefinitionId;
    }

    public BlockDefinition getDefinition() {
        return definition;
    }

    public String getWorldName() {
        return location.worldName();
    }

    public IgnisLocation getLocation() {
        return location;
    }

    public Set<Integer> getDisplayEntityIds() {
        return displayEntityIds;
    }

    public IgnisDataContainer getData() {
        return data;
    }

    public int getTicksLeft() {
        return ticksLeft;
    }

    public void setTicksLeft(int ticksLeft) {
        this.ticksLeft = ticksLeft;
    }

    public void tick() {
        this.ticksLeft--;
    }

    public Object getDisplayEntity() {
        return displayEntity;
    }

    public void setDisplayEntity(Object displayEntity) {
        this.displayEntity = displayEntity;
    }

    public void registerDisplayEntityId(int entityId) {
        this.displayEntityIds.add(entityId);
    }

    public IgnisTask getTask() {
        return task;
    }

    public void setTask(IgnisTask task) {
        this.task = task;
    }

    public void setFlag(String key, boolean value) {
        data.setBoolean(key, value);
    }

    public boolean getFlag(String key) {
        return data.getBoolean(key);
    }
}
