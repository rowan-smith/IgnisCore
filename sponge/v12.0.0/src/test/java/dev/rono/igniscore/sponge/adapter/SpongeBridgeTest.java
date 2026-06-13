package dev.rono.igniscore.sponge.adapter;

import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.PlatformType;
import dev.rono.igniscore.sponge.boot.SpongeV1200Bootloader;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class SpongeBridgeTest {

    @Test
    void materialKeyNormalizesRegistryKeys() {
        assertEquals("minecraft:stone", SpongeBridge.materialKey(ResourceKeys.minecraft("STONE")));
    }

    @Test
    void toIgnisInteractionMapsItemEvents() {
        assertEquals(IgnisInteraction.RIGHT_CLICK_AIR,
                SpongeBridge.toIgnisInteraction(new InteractItemEventStub.Secondary()));
        assertEquals(IgnisInteraction.LEFT_CLICK_AIR,
                SpongeBridge.toIgnisInteraction(new InteractItemEventStub.Primary()));
    }

    @Test
    void ignisItemWrapsStack() {
        ItemStack stack = itemStack(ResourceKeys.minecraft("diamond"), 2);
        SpongeIgnisItem item = SpongeBridge.wrap(stack);

        assertEquals(2, item.getAmount());
        assertFalse(item.isAir());
        assertSame(stack, item.nativeItem());
    }

    @Test
    void unwrapRoundTripsSpongeItem() {
        ItemStack stack = itemStack(ResourceKeys.minecraft("diamond"), 3);
        SpongeIgnisItem wrapped = SpongeBridge.wrap(stack);

        assertSame(stack, SpongeBridge.unwrap(wrapped));
    }

    @Test
    void wrapReturnsNullForEmptyStack() {
        ItemStack stack = itemStack(ResourceKeys.minecraft("air"), 0);
        assertTrue(stack.isEmpty());
        assertNull(SpongeBridge.wrap(stack));
    }

    @Test
    void bootloaderMetadata() {
        SpongeV1200Bootloader bootloader = new SpongeV1200Bootloader();

        assertEquals("sponge-v12.0.0", bootloader.id());
        assertEquals(PlatformType.SPONGE, bootloader.platformType());
        assertEquals("1.21.x", bootloader.minecraftVersionRange());
        assertEquals(200, bootloader.priority());
    }

    @Test
    void bootloaderRejectsNonSpongeHosts() {
        SpongeV1200Bootloader bootloader = new SpongeV1200Bootloader();
        assertFalse(bootloader.canBoot(new MarkedHost()));
        assertFalse(bootloader.canBoot("not-a-plugin-host"));
    }

    private static ItemType itemType(ResourceKey key) {
        return (ItemType) Proxy.newProxyInstance(
                ItemType.class.getClassLoader(),
                new Class<?>[]{ItemType.class},
                new ItemTypeHandler(key));
    }

    private static ItemStack itemStack(ResourceKey key, int quantity) {
        ItemType type = itemType(key);
        return (ItemStack) Proxy.newProxyInstance(
                ItemStack.class.getClassLoader(),
                new Class<?>[]{ItemStack.class},
                new ItemStackHandler(type, quantity));
    }

    @Plugin("marker-test")
    private static final class MarkedHost {
    }

    private static final class ResourceKeys {
        private ResourceKeys() {
        }

        static ResourceKey minecraft(String path) {
            return new ResourceKey() {
                @Override
                public String namespace() {
                    return "minecraft";
                }

                @Override
                public String value() {
                    return path;
                }
            };
        }
    }

    private static final class ItemTypeHandler implements InvocationHandler {
        private final ResourceKey key;

        ItemTypeHandler(ResourceKey key) {
            this.key = key;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "key" -> key;
                case "block", "container" -> Optional.empty();
                case "maxStackQuantity" -> 64;
                case "rarity" -> null;
                case "isAnyOf" -> false;
                case "asComponent" -> Component.text(key.value());
                case "hashCode" -> key.hashCode();
                case "equals" -> proxy == args[0];
                case "toString" -> "TestItemType[" + key.asString() + "]";
                default -> ItemStackHandler.defaultValue(method.getReturnType());
            };
        }
    }

    private static final class ItemStackHandler implements InvocationHandler {
        private final ItemType type;
        private int quantity;

        ItemStackHandler(ItemType type, int quantity) {
            this.type = type;
            this.quantity = quantity;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "type" -> type;
                case "quantity" -> quantity;
                case "setQuantity" -> {
                    quantity = (int) args[0];
                    yield null;
                }
                case "isEmpty" -> quantity <= 0;
                case "equalTo" -> proxy == args[0];
                case "copy", "asMutable", "asMutableCopy", "asImmutable" -> proxy;
                case "asComponent" -> Component.text("item");
                case "attributeModifiers" -> Collections.emptyList();
                case "addAttributeModifier" -> null;
                case "setRawData" -> null;
                case "validateRawData" -> true;
                case "offer", "offerSingle", "offerAll", "removeSingle", "removeKey",
                     "removeAll", "tryOffer", "remove", "undo", "copyFrom" ->
                        DataTransactionResult.successNoData();
                case "get", "getValue", "getOrNull", "requireValue", "supports", "contains" -> null;
                case "getValues" -> Collections.emptyMap();
                case "keys" -> Collections.emptySet();
                case "toContainer" -> null;
                case "hashCode" -> System.identityHashCode(proxy);
                case "equals" -> proxy == args[0];
                case "toString" -> "TestItemStack[x" + quantity + "]";
                default -> defaultValue(method.getReturnType());
            };
        }

        private static Object defaultValue(Class<?> returnType) {
            if (returnType == boolean.class) {
                return false;
            }
            if (returnType == int.class) {
                return 0;
            }
            if (returnType == long.class) {
                return 0L;
            }
            if (returnType == double.class) {
                return 0.0d;
            }
            if (returnType == float.class) {
                return 0.0f;
            }
            if (returnType == byte.class) {
                return (byte) 0;
            }
            if (returnType == short.class) {
                return (short) 0;
            }
            if (returnType == char.class) {
                return (char) 0;
            }
            if (Collection.class.isAssignableFrom(returnType)) {
                return Collections.emptyList();
            }
            return null;
        }
    }

    private interface InteractItemEventStub extends org.spongepowered.api.event.item.inventory.InteractItemEvent {
        final class Primary implements InteractItemEventStub, org.spongepowered.api.event.item.inventory.InteractItemEvent.Primary {
            @Override
            public org.spongepowered.api.event.Cause cause() {
                return org.spongepowered.api.event.Cause.of(
                        org.spongepowered.api.event.EventContext.empty(), this);
            }

            @Override
            public org.spongepowered.api.item.inventory.ItemStackSnapshot itemStack() {
                return null;
            }
        }

        final class Secondary implements InteractItemEventStub, org.spongepowered.api.event.item.inventory.InteractItemEvent.Secondary {
            private boolean cancelled;

            @Override
            public org.spongepowered.api.event.Cause cause() {
                return org.spongepowered.api.event.Cause.of(
                        org.spongepowered.api.event.EventContext.empty(), this);
            }

            @Override
            public org.spongepowered.api.item.inventory.ItemStackSnapshot itemStack() {
                return null;
            }

            @Override
            public boolean isCancelled() {
                return cancelled;
            }

            @Override
            public void setCancelled(boolean cancel) {
                this.cancelled = cancel;
            }
        }
    }
}
