package dev.rono.igniscore.resourcepack;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VanillaTexturePathsTest {
    @Test
    void mapsBlockFacesToVanillaPaths() {
        assertEquals("minecraft:block/tnt_top", VanillaTexturePaths.blockTexturePath("tnt", "top"));
        assertEquals("minecraft:block/tnt_bottom", VanillaTexturePaths.blockTexturePath("tnt", "bottom"));
        assertEquals("minecraft:block/tnt_side", VanillaTexturePaths.blockTexturePath("tnt", "side"));
        assertEquals("minecraft:block/tnt_side", VanillaTexturePaths.blockTexturePath("tnt", "side-2"));
    }

    @Test
    void mapsItemIdsToVanillaPaths() {
        assertEquals("minecraft:item/diamond", VanillaTexturePaths.itemTexturePath("diamond"));
    }

    @Test
    void mapsFaceKeysToModelTextureKeys() {
        assertEquals("north", VanillaTexturePaths.blockModelTextureKey("side-1"));
        assertEquals("side", VanillaTexturePaths.blockModelTextureKey("side"));
    }
}
