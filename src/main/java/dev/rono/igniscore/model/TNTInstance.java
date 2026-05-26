package dev.rono.igniscore.model;

import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.scheduler.BukkitTask;
import java.util.UUID;

public class TNTInstance {
    private final UUID uuid;
    private final Location location;
    private final TNTDefinition type;
    private int ticksLeft;
    private Display displayEntity;
    private BukkitTask task;

    public TNTInstance(Location location, TNTDefinition type) {
        this.uuid = UUID.randomUUID();
        this.location = location;
        this.type = type;
        this.ticksLeft = type.getFuse();
    }

    public UUID getUuid() { return uuid; }
    public Location getLocation() { return location; }
    public TNTDefinition getType() { return type; }
    public int getTicksLeft() { return ticksLeft; }
    public void setTicksLeft(int ticksLeft) { this.ticksLeft = ticksLeft; }
    public void tick() { this.ticksLeft--; }
    
    public Display getDisplayEntity() { return displayEntity; }
    public void setDisplayEntity(Display displayEntity) { this.displayEntity = displayEntity; }
    
    public BukkitTask getTask() { return task; }
    public void setTask(BukkitTask task) { this.task = task; }
}
