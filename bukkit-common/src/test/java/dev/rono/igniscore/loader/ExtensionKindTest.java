package dev.rono.igniscore.loader;

import dev.rono.igniscore.api.config.YamlDefinitions;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.strategy.IgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisItemStrategy;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void bindsStrategyTypesToDefinitionKinds() {
        assertEquals(IgnisBlockStrategy.class, ExtensionKind.BLOCK.strategyType());
        assertEquals(IgnisItemStrategy.class, ExtensionKind.ITEM.strategyType());
    }

    @Test
    void parsesDefinitionsForEachKind() {
        Map<String, Object> config = YamlDefinitions.loadMap(new ByteArrayInputStream("""
                id: sample
                block:
                  base_material: paper
                item:
                  base_material: snowball
                custom_data:
                  fuse: 40
                """.getBytes(StandardCharsets.UTF_8)));

        BlockDefinition block = (BlockDefinition) ExtensionKind.BLOCK.parseDefinition(
                config, "fallback", 10002, "sample-block");
        ItemDefinition item = (ItemDefinition) ExtensionKind.ITEM.parseDefinition(
                config, "fallback", 20002, "sample-item");

        assertEquals("sample", block.getId());
        assertEquals("sample", item.getId());
        assertEquals(10002, block.getCustomModelData());
        assertEquals(20002, item.getCustomModelData());
    }
}
