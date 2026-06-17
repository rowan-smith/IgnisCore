package dev.rono.extensions.shared.strategy;

import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.util.Locations;

/**
 * Persists item-to-block links in item NBT for remote control tools.
 */
public final class BlockLinkSupport {
    private static final String WORLD = "ignis:link_world";
    private static final String X = "ignis:link_x";
    private static final String Y = "ignis:link_y";
    private static final String Z = "ignis:link_z";
    private static final String TYPE = "ignis:link_type";

    private BlockLinkSupport() {
    }

    public static void link(IgnisNbtService nbt, IgnisItem item, String blockTypeId, IgnisLocation location) {
        if (nbt == null) {
            return;
        }
        IgnisLocation block = Locations.toBlock(location);
        nbt.setItemString(item, WORLD, block.worldName());
        nbt.setItemInt(item, X, (int) block.x());
        nbt.setItemInt(item, Y, (int) block.y());
        nbt.setItemInt(item, Z, (int) block.z());
        nbt.setItemString(item, TYPE, blockTypeId);
    }

    public static boolean hasLink(IgnisNbtService nbt, IgnisItem item) {
        if (nbt == null) {
            return false;
        }
        String world = nbt.getItemString(item, WORLD);
        return world != null && !world.isBlank();
    }

    public static IgnisLocation readLocation(IgnisNbtService nbt, IgnisItem item) {
        if (nbt == null || !hasLink(nbt, item)) {
            return null;
        }
        return new IgnisLocation(
                nbt.getItemString(item, WORLD),
                nbt.getItemInt(item, X, 0),
                nbt.getItemInt(item, Y, 0),
                nbt.getItemInt(item, Z, 0));
    }

    public static String readType(IgnisNbtService nbt, IgnisItem item) {
        if (nbt == null) {
            return "";
        }
        String type = nbt.getItemString(item, TYPE);
        return type == null ? "" : type;
    }

    public static boolean withinRange(IgnisPlayer player, IgnisLocation target, double maxRange) {
        if (target == null) {
            return false;
        }
        IgnisLocation here = player.getLocation();
        if (!here.worldName().equalsIgnoreCase(target.worldName())) {
            return false;
        }
        double dx = here.x() - target.x();
        double dy = here.y() - target.y();
        double dz = here.z() - target.z();
        return dx * dx + dy * dy + dz * dz <= maxRange * maxRange;
    }

    public static void clear(IgnisNbtService nbt, IgnisItem item) {
        if (nbt == null) {
            return;
        }
        nbt.setItemString(item, WORLD, "");
        nbt.setItemString(item, TYPE, "");
    }
}
