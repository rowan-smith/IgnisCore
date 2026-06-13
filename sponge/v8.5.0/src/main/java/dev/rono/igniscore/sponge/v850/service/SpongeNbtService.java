package dev.rono.igniscore.sponge.v850.service;

import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.sponge.v850.adapter.SpongeBridge;
import dev.rono.igniscore.sponge.v850.adapter.SpongeIgnisItem;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.item.inventory.ItemStack;

public class SpongeNbtService implements IgnisNbtService {
    private static final DataQuery ROOT = DataQuery.of("igniscore");

    @Override
    public void setItemString(IgnisItem item, String key, String value) {
        editItem(item, container -> container.set(DataQuery.of(normalizeKey(key)), value));
    }

    @Override
    public String getItemString(IgnisItem item, String key) {
        return readItem(item, container -> container.getString(DataQuery.of(normalizeKey(key))).orElse(null));
    }

    @Override
    public void setItemInt(IgnisItem item, String key, int value) {
        editItem(item, container -> container.set(DataQuery.of(normalizeKey(key)), value));
    }

    @Override
    public int getItemInt(IgnisItem item, String key, int defaultValue) {
        Integer value = readItem(item, container -> container.getInt(DataQuery.of(normalizeKey(key))).orElse(null));
        return value != null ? value : defaultValue;
    }

    @Override
    public void setItemBoolean(IgnisItem item, String key, boolean value) {
        editItem(item, container -> container.set(DataQuery.of(normalizeKey(key)), value));
    }

    @Override
    public boolean getItemBoolean(IgnisItem item, String key, boolean defaultValue) {
        Boolean value = readItem(item, container -> container.getBoolean(DataQuery.of(normalizeKey(key))).orElse(null));
        return value != null ? value : defaultValue;
    }

    @Override
    public void setEntityString(Object nativeEntity, String key, String value) {
        if (nativeEntity instanceof org.spongepowered.api.entity.Entity entity) {
            DataContainer container = entity.toContainer();
            DataContainer ignis = container.getView(ROOT)
                    .map(view -> (DataContainer) view.container())
                    .orElseGet(DataContainer::createNew);
            ignis.set(DataQuery.of(normalizeKey(key)), value);
            container.set(ROOT, ignis);
        }
    }

    @Override
    public String getEntityString(Object nativeEntity, String key) {
        if (!(nativeEntity instanceof org.spongepowered.api.entity.Entity entity)) {
            return null;
        }
        return entity.toContainer()
                .getView(ROOT)
                .flatMap(view -> view.getString(DataQuery.of(normalizeKey(key))))
                .orElse(null);
    }

    private static void editItem(IgnisItem item, java.util.function.Consumer<DataContainer> action) {
        ItemStack stack = SpongeBridge.unwrap(item);
        if (stack == null || stack.isEmpty()) {
            return;
        }
        DataContainer container = stack.toContainer();
        DataContainer ignis = container.getView(ROOT)
                .map(view -> (DataContainer) view.container())
                .orElseGet(DataContainer::createNew);
        action.accept(ignis);
        container.set(ROOT, ignis);
        ItemStack rebuilt = ItemStack.builder().fromContainer(container).build();
        if (item instanceof SpongeIgnisItem spongeItem) {
            spongeItem.setHandle(rebuilt);
        }
    }

    private static <T> T readItem(IgnisItem item, java.util.function.Function<DataContainer, T> action) {
        ItemStack stack = SpongeBridge.unwrap(item);
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        return stack.toContainer()
                .getView(ROOT)
                .map(view -> action.apply(view.container()))
                .orElse(null);
    }

    private static String normalizeKey(String key) {
        return key.startsWith("ignis:") ? key.substring("ignis:".length()) : key;
    }
}
