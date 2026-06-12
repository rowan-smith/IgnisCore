package dev.rono.igniscore.loader;

import dev.rono.igniscore.api.config.DefinitionParser;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;

import java.util.Map;

enum ExtensionKind {
    BLOCK("blocks", "bundled/blocks", "block-extension.yml", 10001),
    ITEM("items", "bundled/items", "item-extension.yml", 20001);

    private final String folderName;
    private final String bundledResourcePrefix;
    private final String manifestFileName;
    private final int modelDataStart;

    ExtensionKind(String folderName, String bundledResourcePrefix, String manifestFileName, int modelDataStart) {
        this.folderName = folderName;
        this.bundledResourcePrefix = bundledResourcePrefix;
        this.manifestFileName = manifestFileName;
        this.modelDataStart = modelDataStart;
    }

    String folderName() {
        return folderName;
    }

    String bundledResourcePrefix() {
        return bundledResourcePrefix;
    }

    String manifestFileName() {
        return manifestFileName;
    }

    int modelDataStart() {
        return modelDataStart;
    }

    BlockDefinition parseBlock(Map<String, Object> config, String defaultId, int modelData, String extensionId) {
        if (this != BLOCK) {
            throw new IllegalStateException("Not a block extension kind");
        }
        return DefinitionParser.parseBlock(config, defaultId, modelData, extensionId);
    }

    ItemDefinition parseItem(Map<String, Object> config, String defaultId, int modelData, String extensionId) {
        if (this != ITEM) {
            throw new IllegalStateException("Not an item extension kind");
        }
        return DefinitionParser.parseItem(config, defaultId, modelData, extensionId);
    }
}
