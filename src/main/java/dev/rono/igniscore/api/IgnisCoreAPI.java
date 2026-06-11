package dev.rono.igniscore.api;

import dev.rono.igniscore.IgnisCoreApplication;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.core.ExtensionBootstrap;
import dev.rono.igniscore.loader.ContentPackLoader;
import dev.rono.igniscore.loader.StrategyPluginLoader;
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
    private static IgnisCoreApplication application;

    public static void init(IgnisCoreApplication instance) {
        application = instance;
    }

    public static BlockManager getBlockManager() {
        return application.getBlockManager();
    }

    public static NBTService getNbtService() {
        return application.getNbtService();
    }

    public static ProtocolService getProtocolService() {
        return application.getProtocolService();
    }

    public static RuntimeBlockService getRuntimeBlockService() {
        return application.getRuntimeBlockService();
    }

    public static VisualEffectService getVisualEffectService() {
        return application.getVisualEffectService();
    }

    public static RuntimeBlockInstance triggerBlock(Location location, String typeId, Object context) {
        return application.getBlockManager().triggerBlock(location, typeId, context);
    }

    public static Collection<RuntimeBlockInstance> getActiveBlocks() {
        return application.getBlockManager().getActiveBlocks();
    }

    public static Map<String, BlockDefinition> getBlockTypes() {
        return application.getBlockManager().getBlockTypes();
    }

    public static IgnisStrategyRegistry getStrategyRegistry() {
        return application.getStrategyRegistry();
    }

    public static void reloadExtensions() {
        application.reloadExtensions();
    }
}
