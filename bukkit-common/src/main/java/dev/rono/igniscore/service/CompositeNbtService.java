package dev.rono.igniscore.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.service.IgnisNbtService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Routes item NBT to NBT-API when present, otherwise PDC. Entity data requires NBT-API.
 */
public final class CompositeNbtService implements IgnisNbtService {
    private final NBTService nbtApi;
    private final PdcNbtService pdc;
    private final boolean nbtApiEnabled;

    @Inject
    public CompositeNbtService(Plugin plugin, NBTService nbtApi, PdcNbtService pdc) {
        this.nbtApi = nbtApi;
        this.pdc = pdc;
        this.nbtApiEnabled = Bukkit.getPluginManager().isPluginEnabled("NBTAPI");
        if (nbtApiEnabled) {
            plugin.getLogger().info("NBT integration enabled via NBT-API.");
        } else {
            plugin.getLogger().info("NBT-API not found. Item data uses PDC fallback; entity NBT disabled.");
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String providerName() {
        return nbtApiEnabled ? "NBT-API" : pdc.providerName();
    }

    @Override
    public boolean supportsEntityData() {
        return nbtApiEnabled;
    }

    @Override
    public void setItemString(IgnisItem item, String key, String value) {
        delegateItem().setItemString(item, key, value);
    }

    @Override
    public String getItemString(IgnisItem item, String key) {
        return delegateItem().getItemString(item, key);
    }

    @Override
    public void setItemInt(IgnisItem item, String key, int value) {
        delegateItem().setItemInt(item, key, value);
    }

    @Override
    public int getItemInt(IgnisItem item, String key, int defaultValue) {
        return delegateItem().getItemInt(item, key, defaultValue);
    }

    @Override
    public void setItemBoolean(IgnisItem item, String key, boolean value) {
        delegateItem().setItemBoolean(item, key, value);
    }

    @Override
    public boolean getItemBoolean(IgnisItem item, String key, boolean defaultValue) {
        return delegateItem().getItemBoolean(item, key, defaultValue);
    }

    @Override
    public void setEntityString(Object nativeEntity, String key, String value) {
        if (nbtApiEnabled) {
            nbtApi.setEntityString(nativeEntity, key, value);
        }
    }

    @Override
    public String getEntityString(Object nativeEntity, String key) {
        return nbtApiEnabled ? nbtApi.getEntityString(nativeEntity, key) : null;
    }

    private IgnisNbtService delegateItem() {
        return nbtApiEnabled ? nbtApi : pdc;
    }
}
