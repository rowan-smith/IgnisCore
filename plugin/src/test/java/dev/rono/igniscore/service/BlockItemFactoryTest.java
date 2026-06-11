package dev.rono.igniscore.service;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.manager.BlockManager;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BlockItemFactoryTest {
    @Test
    void rejectsUnknownBlockTypes() {
        BlockItemFactory factory = new BlockItemFactory(emptyBlockManager(), new NBTService());

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> factory.createBlockItem("missing"));
        assertEquals("Unknown block type: missing", error.getMessage());
    }

    private static BlockManager emptyBlockManager() {
        return new BlockManager(null, null, null, null) {
            @Override
            public Map<String, BlockDefinition> getBlockTypes() {
                return Map.of();
            }
        };
    }
}
