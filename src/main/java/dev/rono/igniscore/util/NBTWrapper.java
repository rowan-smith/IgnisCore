package dev.rono.igniscore.util;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;

/**
 * Base class for structured NBT data wrappers.
 */
public abstract class NBTWrapper {
    protected final ReadWriteNBT nbt;

    protected NBTWrapper(ReadWriteNBT nbt) {
        this.nbt = nbt;
    }

    /**
     * @return The underlying NBT container.
     */
    public ReadWriteNBT getRaw() {
        return nbt;
    }
}
