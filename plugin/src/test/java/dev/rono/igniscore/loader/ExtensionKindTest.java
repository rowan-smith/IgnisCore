package dev.rono.igniscore.loader;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExtensionKindTest {
    @Test
    void exposesFolderAndModelDataConventions() {
        assertEquals("blocks", ExtensionKind.BLOCK.folderName());
        assertEquals("items", ExtensionKind.ITEM.folderName());
        assertEquals("block-extension.yml", ExtensionKind.BLOCK.manifestFileName());
        assertEquals("item-extension.yml", ExtensionKind.ITEM.manifestFileName());
        assertEquals(10001, ExtensionKind.BLOCK.modelDataStart());
        assertEquals(20001, ExtensionKind.ITEM.modelDataStart());
    }

    @Test
    void parsesDefinitionsForMatchingKindOnly() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new java.io.StringReader("""
                id: sample
                block:
                  base_material: paper
                item:
                  base_material: snowball
                behavior:
                  strategy: sample
                """));

        BlockDefinition block = ExtensionKind.BLOCK.parseBlock(config, "fallback", 10002, "sample-block");
        ItemDefinition item = ExtensionKind.ITEM.parseItem(config, "fallback", 20002, "sample-item");

        assertEquals("sample", block.getId());
        assertEquals("sample", item.getId());
        assertEquals(10002, block.getCustomModelData());
        assertEquals(20002, item.getCustomModelData());
    }

    @Test
    void rejectsCrossKindParsing() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new java.io.StringReader("id: sample"));

        assertThrows(IllegalStateException.class,
                () -> ExtensionKind.ITEM.parseBlock(config, "sample", 10001, "sample-block"));
        assertThrows(IllegalStateException.class,
                () -> ExtensionKind.BLOCK.parseItem(config, "sample", 20001, "sample-item"));
    }
}
