package dev.rono.igniscore.api.model;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.scheduler.BukkitTask;

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
    private final Location location;
    private final Set<Integer> displayEntityIds = new HashSet<>();
    private final ReadWriteNBT data;
    
    // Runtime state
    private int ticksLeft;
    private Display displayEntity;
    private BukkitTask task;

    public RuntimeBlockInstance(UUID uuid, BlockDefinition definition, Location location) {
        this.uuid = uuid;
        this.definition = definition;
        this.blockDefinitionId = definition.getId();
        this.location = location;
        this.data = new NBTContainer();
        this.ticksLeft = definition.getFuse();
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

    public World getWorld() {
        return location.getWorld();
    }

    public Location getLocation() {
        return location;
    }

    public Set<Integer> getDisplayEntityIds() {
        return displayEntityIds;
    }

    /**
     * @return The NBT data container for this instance.
     */
    public ReadWriteNBT getData() {
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

    public Display getDisplayEntity() {
        return displayEntity;
    }

    public void setDisplayEntity(Display displayEntity) {
        this.displayEntity = displayEntity;
        if (displayEntity != null) {
            this.displayEntityIds.add(displayEntity.getEntityId());
        }
    }

    public BukkitTask getTask() {
        return task;
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }

    // Convenience methods for strategy data
    public void setFlag(String key, boolean value) {
        data.setBoolean(key, value);
    }

    public boolean getFlag(String key) {
        return data.getBoolean(key);
    }
}
