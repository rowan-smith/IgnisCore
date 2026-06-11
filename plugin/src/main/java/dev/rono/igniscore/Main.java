package dev.rono.igniscore;

import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.rono.igniscore.api.IgnisCoreAPI;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.resourcepack.ResourcePackService;
import dev.rono.igniscore.service.NBTService;
import dev.rono.igniscore.service.ProtocolService;
import dev.rono.igniscore.service.RuntimeBlockService;
import dev.rono.igniscore.service.VisualEffectService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    private Injector injector;
    private IgnisCoreApplication application;
    private boolean debugEnabled = false;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        injector = Guice.createInjector(new IgnisCoreModule(this));
        application = injector.getInstance(IgnisCoreApplication.class);
        IgnisCoreAPI.init(application);
        application.enable();
    }

    @Override
    public void onDisable() {
        if (application != null) {
            application.disable();
        }
    }

    public ItemStack createBlockItem(String typeId) {
        return application.getBlockItemFactory().createBlockItem(typeId);
    }

    public Component message(String message) {
        return miniMessage.deserialize(message);
    }

    public void debug(String message) {
        if (debugEnabled) {
            getLogger().info("[DEBUG] " + message);
        }
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public BlockManager getBlockManager() {
        return application.getBlockManager();
    }

    public NBTService getNbtService() {
        return application.getNbtServiceImpl();
    }

    public ProtocolService getProtocolService() {
        return application.getProtocolServiceImpl();
    }

    public RuntimeBlockService getRuntimeBlockService() {
        return application.getRuntimeBlockService();
    }

    public VisualEffectService getVisualEffectService() {
        return application.getVisualEffectService();
    }

    public ResourcePackService getResourcePackService() {
        return application.getResourcePackService();
    }
}
