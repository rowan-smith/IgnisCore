package dev.rono.igniscore.service;

import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.support.TestDefinitions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URLClassLoader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemFactoryTest {
    private ItemFactory factory;

    @BeforeEach
    void setUp() throws Exception {
        ItemManager itemManager = new ItemManager();
        try (URLClassLoader classLoader = new URLClassLoader(new java.net.URL[0], getClass().getClassLoader())) {
            itemManager.loadFromExtensions(List.of(
                    TestDefinitions.loadedItem(TestDefinitions.item("grenade", "grenade"), classLoader)));
        }
        factory = new ItemFactory(itemManager, new NBTService());
    }

    @Test
    void rejectsUnknownItemTypes() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> factory.createItem("missing"));
        assertEquals("Unknown item type: missing", error.getMessage());
    }
}
