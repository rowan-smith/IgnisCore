package dev.rono.igniscore.api;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IgnisCoreAPITest {
    @AfterEach
    void resetApi() {
        IgnisCoreAPI.init(null);
    }

    @Test
    void delegatesToInitializedFacade() {
        RecordingFacade facade = new RecordingFacade();
        IgnisCoreAPI.init(facade);

        IgnisCoreAPI.reloadExtensions();
        IgnisCoreAPI.createItem("grenade");
        IgnisCoreAPI.getBlockTypes();

        assertEquals(1, facade.reloadCount);
        assertEquals("grenade", facade.lastItemType);
        assertSame(facade.blocks, IgnisCoreAPI.getBlockTypes());
        assertSame(facade.registry, IgnisCoreAPI.getStrategyRegistry());
        assertSame(facade.nbtService, IgnisCoreAPI.getNbtService());
        assertSame(facade.protocolService, IgnisCoreAPI.getProtocolService());
        assertSame(facade.effectService, IgnisCoreAPI.getEffectService());
    }

    @Test
    void failsFastWhenFacadeIsNotInitialized() {
        IllegalStateException error = assertThrows(IllegalStateException.class, IgnisCoreAPI::getItemTypes);
        assertEquals("IgnisCoreAPI has not been initialized", error.getMessage());
    }

    private static final class RecordingFacade implements IgnisCoreFacade {
        private final Map<String, BlockDefinition> blocks = Map.of();
        private final Map<String, ItemDefinition> items = Map.of();
        private final IgnisStrategyRegistry registry = new IgnisStrategyRegistry() {
            @Override
            public void register(dev.rono.igniscore.api.strategy.IgnisStrategy strategy) {
            }

            @Override
            public void register(dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor descriptor,
                                 dev.rono.igniscore.api.strategy.IgnisStrategy strategy) {
            }

            @Override
            public void unregister(String strategyId) {
            }

            @Override
            public void unregisterBySource(String sourcePluginId) {
            }

            @Override
            public java.util.Optional<dev.rono.igniscore.api.strategy.IgnisStrategy> find(String strategyId) {
                return java.util.Optional.empty();
            }

            @Override
            public dev.rono.igniscore.api.strategy.IgnisStrategy get(String strategyId) {
                return null;
            }

            @Override
            public Collection<dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor> getDescriptors() {
                return java.util.List.of();
            }

            @Override
            public boolean isRegistered(String strategyId) {
                return false;
            }
        };
        private final IgnisNbtService nbtService = new IgnisNbtService() {
            @Override
            public void editItem(ItemStack item, java.util.function.Consumer<de.tr7zw.nbtapi.iface.ReadWriteItemNBT> action) {
            }

            @Override
            public <T> T readItem(ItemStack item, java.util.function.Function<de.tr7zw.nbtapi.iface.ReadableItemNBT, T> action) {
                return null;
            }

            @Override
            public void editEntity(org.bukkit.entity.Entity entity, java.util.function.Consumer<de.tr7zw.nbtapi.iface.ReadWriteNBT> action) {
            }

            @Override
            public <T> T readEntity(org.bukkit.entity.Entity entity, java.util.function.Function<de.tr7zw.nbtapi.iface.ReadableNBT, T> action) {
                return null;
            }
        };
        private final IgnisProtocolService protocolService = new IgnisProtocolService() {
            @Override
            public boolean isEnabled() {
                return false;
            }

            @Override
            public void sendFakeExplosion(Location location, float power, java.util.Collection<org.bukkit.entity.Player> players) {
            }
        };
        private final IgnisEffectService effectService = new IgnisEffectService() {
            @Override
            public void playSound(Location location, String soundName, float volume, float pitch) {
            }

            @Override
            public void playFakeExplosion(Location location, float power, java.util.Collection<org.bukkit.entity.Player> players) {
            }

            @Override
            public void showBlockPreview(org.bukkit.entity.Player player, Location location, org.bukkit.Material material) {
            }
        };

        private int reloadCount;
        private String lastItemType;

        @Override
        public Map<String, BlockDefinition> getBlockTypes() {
            return blocks;
        }

        @Override
        public Map<String, ItemDefinition> getItemTypes() {
            return items;
        }

        @Override
        public RuntimeBlockInstance triggerBlock(Location location, String typeId, Object context) {
            return null;
        }

        @Override
        public String getPlacedBlockType(Location location) {
            return null;
        }

        @Override
        public Collection<RuntimeBlockInstance> getActiveBlocks() {
            return java.util.List.of();
        }

        @Override
        public ItemStack createBlockItem(String typeId) {
            return null;
        }

        @Override
        public ItemStack createItem(String typeId) {
            lastItemType = typeId;
            return null;
        }

        @Override
        public IgnisStrategyRegistry getStrategyRegistry() {
            return registry;
        }

        @Override
        public IgnisNbtService getNbtService() {
            return nbtService;
        }

        @Override
        public IgnisProtocolService getProtocolService() {
            return protocolService;
        }

        @Override
        public IgnisEffectService getEffectService() {
            return effectService;
        }

        @Override
        public void reloadExtensions() {
            reloadCount++;
        }
    }
}
