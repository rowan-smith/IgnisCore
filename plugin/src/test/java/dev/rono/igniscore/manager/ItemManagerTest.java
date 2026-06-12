package dev.rono.igniscore.manager;

import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.loader.LoadedExtension;
import dev.rono.igniscore.support.TestDefinitions;
import org.junit.jupiter.api.Test;

import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemManagerTest {
    @Test
    void loadsAndReplacesItemDefinitionsFromExtensions() throws Exception {
        ItemManager manager = new ItemManager();
        try (URLClassLoader classLoader = new URLClassLoader(new java.net.URL[0], getClass().getClassLoader())) {
            LoadedExtension<ItemDefinition> grenade = TestDefinitions.loadedItem(
                    TestDefinitions.item("grenade"), classLoader);
            LoadedExtension<ItemDefinition> detonator = TestDefinitions.loadedItem(
                    TestDefinitions.item("detonator"), classLoader);

            manager.loadFromExtensions(List.of(grenade, detonator));

            Map<String, ItemDefinition> types = manager.getItemTypes();
            assertEquals(2, types.size());
            assertEquals("grenade-item", types.get("grenade").getExtensionId());
            assertEquals("detonator-item", types.get("detonator").getExtensionId());
            assertThrows(UnsupportedOperationException.class, () -> types.put("hack", grenade.getDefinition()));

            manager.loadFromExtensions(List.of(grenade));
            assertEquals(1, manager.getItemTypes().size());
            assertTrue(manager.getItemTypes().containsKey("grenade"));
        }
    }

    @Test
    void clearsDefinitionsWhenReloadingEmptyList() throws Exception {
        ItemManager manager = new ItemManager();
        try (URLClassLoader classLoader = new URLClassLoader(new java.net.URL[0], getClass().getClassLoader())) {
            manager.loadFromExtensions(List.of(
                    TestDefinitions.loadedItem(TestDefinitions.item("grenade"), classLoader)));
            manager.loadFromExtensions(List.of());
            assertTrue(manager.getItemTypes().isEmpty());
        }
    }
}
