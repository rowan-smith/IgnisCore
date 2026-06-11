package dev.rono.igniscore.support;

import dev.rono.igniscore.api.service.IgnisNbtService;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Consumer;
import java.util.function.Function;

public final class PdcBackedNbtService implements IgnisNbtService {
    private static final NamespacedKey NAMESPACE = NamespacedKey.fromString("ignis:nbt");

    @Override
    public void editItem(ItemStack item, Consumer<ReadWriteItemNBT> action) {
        if (item == null || item.getType().isAir()) {
            return;
        }
        action.accept(createItemNbtView(item, true));
    }

    @Override
    public <T> T readItem(ItemStack item, Function<ReadableItemNBT, T> action) {
        if (item == null || item.getType().isAir()) {
            return null;
        }
        return action.apply(createItemNbtView(item, false));
    }

    @Override
    public void editEntity(Entity entity, Consumer<ReadWriteNBT> action) {
        throw new UnsupportedOperationException("Entity NBT is not supported in PdcBackedNbtService");
    }

    @Override
    public <T> T readEntity(Entity entity, Function<ReadableNBT, T> action) {
        throw new UnsupportedOperationException("Entity NBT is not supported in PdcBackedNbtService");
    }

    public ReadWriteNBT createCompound() {
        return (ReadWriteNBT) Proxy.newProxyInstance(
                ReadWriteNBT.class.getClassLoader(),
                new Class<?>[]{ReadWriteNBT.class},
                new InMemoryCompoundHandler());
    }

    private ReadWriteItemNBT createItemNbtView(ItemStack item, boolean writable) {
        return (ReadWriteItemNBT) Proxy.newProxyInstance(
                ReadWriteItemNBT.class.getClassLoader(),
                new Class<?>[]{ReadWriteItemNBT.class},
                new ItemNbtHandler(item, writable));
    }

    private static NamespacedKey toKey(String nbtKey) {
        String suffix = nbtKey.startsWith("ignis:") ? nbtKey.substring("ignis:".length()) : nbtKey;
        return new NamespacedKey(NAMESPACE.getNamespace(), suffix);
    }

    private static final class ItemNbtHandler implements InvocationHandler {
        private final ItemStack item;
        private final boolean writable;

        private ItemNbtHandler(ItemStack item, boolean writable) {
            this.item = item;
            this.writable = writable;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            String name = method.getName();
            return switch (name) {
                case "setString" -> {
                    requireWritable();
                    setString((String) args[0], (String) args[1]);
                    yield null;
                }
                case "setInteger" -> {
                    requireWritable();
                    setInteger((String) args[0], (Integer) args[1]);
                    yield null;
                }
                case "getString" -> getString((String) args[0]);
                case "getInteger" -> getInteger((String) args[0]);
                case "hasNBTData" -> hasNbtData();
                case "toString" -> "PdcBackedItemNbt";
                case "hashCode" -> System.identityHashCode(proxy);
                case "equals" -> proxy == args[0];
                default -> defaultValue(method);
            };
        }

        private void requireWritable() {
            if (!writable) {
                throw new UnsupportedOperationException("Read-only item NBT view");
            }
        }

        private PersistentDataContainer container() {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                throw new IllegalStateException("ItemStack has no item meta");
            }
            return meta.getPersistentDataContainer();
        }

        private void commit(ItemMeta meta) {
            item.setItemMeta(meta);
        }

        private void setString(String key, String value) {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                return;
            }
            meta.getPersistentDataContainer().set(toKey(key), PersistentDataType.STRING, value);
            commit(meta);
        }

        private void setInteger(String key, Integer value) {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                return;
            }
            meta.getPersistentDataContainer().set(toKey(key), PersistentDataType.INTEGER, value);
            commit(meta);
        }

        private String getString(String key) {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                return null;
            }
            return meta.getPersistentDataContainer().get(toKey(key), PersistentDataType.STRING);
        }

        private Integer getInteger(String key) {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                return null;
            }
            return meta.getPersistentDataContainer().get(toKey(key), PersistentDataType.INTEGER);
        }

        private boolean hasNbtData() {
            ItemMeta meta = item.getItemMeta();
            return meta != null && !meta.getPersistentDataContainer().isEmpty();
        }

        private Object defaultValue(Method method) {
            Class<?> returnType = method.getReturnType();
            if (returnType == boolean.class) {
                return false;
            }
            if (returnType == int.class) {
                return 0;
            }
            if (returnType == float.class) {
                return 0.0f;
            }
            if (returnType == double.class) {
                return 0.0d;
            }
            if (returnType == long.class) {
                return 0L;
            }
            if (returnType == short.class) {
                return (short) 0;
            }
            if (returnType == byte.class) {
                return (byte) 0;
            }
            return null;
        }
    }

    private static final class InMemoryCompoundHandler implements InvocationHandler {
        private final java.util.Map<String, Object> values = new java.util.HashMap<>();

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            String name = method.getName();
            return switch (name) {
                case "setString" -> {
                    values.put((String) args[0], args[1]);
                    yield null;
                }
                case "setFloat" -> {
                    values.put((String) args[0], args[1]);
                    yield null;
                }
                case "getString" -> (String) values.get(args[0]);
                case "getFloat" -> {
                    Object value = values.get(args[0]);
                    yield value instanceof Float floatValue ? floatValue : null;
                }
                case "getBoolean" -> {
                    Object value = values.get(args[0]);
                    yield value instanceof Boolean booleanValue && booleanValue;
                }
                case "setBoolean" -> {
                    values.put((String) args[0], args[1]);
                    yield null;
                }
                case "toString" -> "InMemoryCompound";
                case "hashCode" -> System.identityHashCode(proxy);
                case "equals" -> proxy == args[0];
                default -> defaultValue(method);
            };
        }

        private Object defaultValue(Method method) {
            Class<?> returnType = method.getReturnType();
            if (returnType == boolean.class) {
                return false;
            }
            if (returnType == float.class) {
                return 0.0f;
            }
            return null;
        }
    }
}
