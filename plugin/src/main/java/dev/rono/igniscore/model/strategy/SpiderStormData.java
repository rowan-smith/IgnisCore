package dev.rono.igniscore.model.strategy;

import dev.rono.igniscore.util.NBTWrapper;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;

/**
 * Example structured metadata for Spider Storm behavior.
 */
public class SpiderStormData extends NBTWrapper {

    public SpiderStormData(ReadWriteNBT nbt) {
        super(nbt);
    }

    public void setSpiderCount(int count) {
        nbt.setInteger("spider_count", count);
    }

    public int getSpiderCount() {
        return nbt.getInteger("spider_count");
    }

    public void setRadius(double radius) {
        nbt.setDouble("radius", radius);
    }

    public double getRadius() {
        return nbt.getDouble("radius");
    }

    public void setAggressionMode(String mode) {
        nbt.setString("aggression_mode", mode);
    }

    public String getAggressionMode() {
        return nbt.getString("aggression_mode");
    }
}
