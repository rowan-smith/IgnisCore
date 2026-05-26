package dev.rono.igniscore.api;

import dev.rono.igniscore.Main;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.model.RuntimeBlockInstance;
import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.service.NBTService;
import dev.rono.igniscore.service.ProtocolService;
import dev.rono.igniscore.service.RuntimeBlockService;
import dev.rono.igniscore.service.VisualEffectService;
import org.bukkit.Location;

import java.util.Collection;
import java.util.Map;

public class IgnisCoreAPI {
    private static Main plugin;

    public static void init(Main instance) {
        plugin = instance;
    }

    public static BlockManager getBlockManager() { return plugin.getBlockManager(); }
    public static NBTService getNbtService() { return plugin.getNbtService(); }
    public static ProtocolService getProtocolService() { return plugin.getProtocolService(); }
    public static RuntimeBlockService getRuntimeBlockService() { return plugin.getRuntimeBlockService(); }
    public static VisualEffectService getVisualEffectService() { return plugin.getVisualEffectService(); }

    public static RuntimeBlockInstance triggerBlock(Location location, String typeId, Object context) {
        return plugin.getBlockManager().triggerBlock(location, typeId, context);
    }

    public static Collection<RuntimeBlockInstance> getActiveBlocks() {
        return plugin.getBlockManager().getActiveBlocks();
    }

    public static Map<String, BlockDefinition> getBlockTypes() {
        return plugin.getBlockManager().getBlockTypes();
    }
}
