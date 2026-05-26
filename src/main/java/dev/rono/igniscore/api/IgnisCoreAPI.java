package dev.rono.igniscore.api;

import dev.rono.igniscore.manager.TNTManager;
import dev.rono.igniscore.model.TNTInstance;
import dev.rono.igniscore.model.TNTDefinition;
import org.bukkit.Location;

import java.util.Collection;
import java.util.Map;

public class IgnisCoreAPI {
    private static TNTManager manager;

    public static void init(TNTManager tntManager) {
        manager = tntManager;
    }

    public static TNTInstance spawnTNT(Location location, String typeId) {
        return manager.spawnTNT(location, typeId);
    }

    public static Collection<TNTInstance> getActiveTNTs() {
        return manager.getActiveTNTs();
    }

    public static Map<String, TNTDefinition> getTntTypes() {
        return manager.getTntTypes();
    }
}
