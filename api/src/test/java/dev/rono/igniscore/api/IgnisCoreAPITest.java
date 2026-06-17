package dev.rono.igniscore.api;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.service.IgnisEffectService;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.service.IgnisProtocolService;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
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
            public boolean isEnabled() {
                return true;
            }

            @Override
            public String providerName() {
                return "test";
            }

            @Override
            public boolean supportsEntityData() {
                return true;
            }

            @Override
            public void setItemString(IgnisItem item, String key, String value) {
            }

            @Override
            public String getItemString(IgnisItem item, String key) {
                return null;
            }

            @Override
            public void setItemInt(IgnisItem item, String key, int value) {
            }

            @Override
            public int getItemInt(IgnisItem item, String key, int defaultValue) {
                return defaultValue;
            }

            @Override
            public void setItemBoolean(IgnisItem item, String key, boolean value) {
            }

            @Override
            public boolean getItemBoolean(IgnisItem item, String key, boolean defaultValue) {
                return defaultValue;
            }

            @Override
            public void setEntityString(Object nativeEntity, String key, String value) {
            }

            @Override
            public String getEntityString(Object nativeEntity, String key) {
                return null;
            }
        };
        private final IgnisProtocolService protocolService = new IgnisProtocolService() {
            @Override
            public boolean isEnabled() {
                return false;
            }

            @Override
            public String providerName() {
                return "test";
            }

            @Override
            public void sendFakeExplosion(IgnisLocation location, float power, Collection<IgnisPlayer> players) {
            }
        };
        private final IgnisEffectService effectService = new IgnisEffectService() {
            @Override
            public void playSound(IgnisLocation location, String soundName, float volume, float pitch) {
            }

            @Override
            public void playFakeExplosion(IgnisLocation location, float power, Collection<IgnisPlayer> players) {
            }

            @Override
            public void showBlockPreview(IgnisPlayer player, IgnisLocation location, String materialKey) {
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
        public RuntimeBlockInstance triggerBlock(IgnisLocation location, String typeId, Object context) {
            return null;
        }

        @Override
        public RuntimeBlockInstance ignitePlacedBlock(IgnisLocation location, Object context) {
            return null;
        }

        @Override
        public String getPlacedBlockType(IgnisLocation location) {
            return null;
        }

        @Override
        public Collection<RuntimeBlockInstance> getActiveBlocks() {
            return java.util.List.of();
        }

        @Override
        public IgnisItem createBlockItem(String typeId) {
            return null;
        }

        @Override
        public IgnisItem createItem(String typeId) {
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
        public dev.rono.igniscore.api.service.IgnisRegionService getRegionService() {
            return null;
        }

        @Override
        public dev.rono.igniscore.api.service.IgnisHologramService getHologramService() {
            return null;
        }

        @Override
        public dev.rono.igniscore.api.service.IgnisNpcService getNpcService() {
            return null;
        }

        @Override
        public dev.rono.igniscore.api.integration.IgnisIntegrationRegistry getIntegrationRegistry() {
            return null;
        }

        @Override
        public void reloadExtensions() {
            reloadCount++;
        }
    }
}
